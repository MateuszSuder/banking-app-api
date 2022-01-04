package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.model.Currency;
import banking.application.model.input.LoanInput;
import banking.application.serviceInterface.ILoanService;
import banking.application.util.Currencies;
import banking.application.util.TransactionType;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service handling loan operations
 */
@Service
public class LoanService extends EntryService implements ILoanService {

	/**
	 * Initialization, check for not filled loan payments
	 */
	@Bean(initMethod = "init")
	public void init() {
		this.loanHandler();
	}

	/**
	 * Method to check if account has active loan
	 * @param iban iban of account to check
	 * @return true if it has active loan, else false
	 */
    @Override
    public boolean accountHasActiveLoan(String iban) {
        Optional<Boolean> active = this.bankAccountRepository.checkIfAccountIsActive(iban);

        return active.orElse(false);
    }

	/**
	 * Method handling creating new loan and binding it to user
	 * @param iban iban of account wanting to take loan
	 * @param loanInput loan input
	 * @return created loan
	 * @throws ThrowableErrorResponse if account has already active loan
	 */
    @Override
    @Transactional
    public Loan takeLoan(String iban, LoanInput loanInput) throws ThrowableErrorResponse {
        if(this.accountHasActiveLoan(iban)) {
            throw new ThrowableErrorResponse(
                    "Account has active loan",
                    "Account can have only one active loan at the time",
                    409);
        }

		// Calc amount with interest and per month
        double loanAmountWithInterest = loanInput.getLoanAmount() + loanInput.getLoanAmount() * (loanInput.getLoanRate() / 100);
        double loanAmountPerMonth = loanAmountWithInterest / loanInput.getLoanLength();

		// Round results
        BigDecimal roundedPerMonth = new BigDecimal(Double.toString(loanAmountPerMonth));
        roundedPerMonth = roundedPerMonth.setScale(2, RoundingMode.HALF_UP);

		// Create new list of installments
        ArrayList<Installment> installments = new ArrayList<>();

		// Create calendar instance
        Calendar cal = Calendar.getInstance();
		// Fill installments list
        for(int i = 0; i < loanInput.getLoanLength(); i++) {
            cal.add(Calendar.MONTH, 1);
            installments.add(new Installment(
                    i,
                    roundedPerMonth.doubleValue(),
                    roundedPerMonth.doubleValue(),
                    cal.getTime(),
                    null)
            );
        }

		// Create new calendar instance
        cal = Calendar.getInstance();
		// Get starting day of loan
        Date startedAt = cal.getTime();
		// Add one month to date
        cal.add(Calendar.MONTH, loanInput.getLoanLength());
		// Get end day of loan
        Date endsAt = cal.getTime();

		// Create Loan instance
        Loan loan = new Loan(
                startedAt,
                endsAt,
                loanInput.getLoanAmount(),
                loanAmountWithInterest,
                installments,
                0,
                Boolean.TRUE.equals(loanInput.getAutoPayment())
        );

		// Find and account and add loan instance to it
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(iban));
        Update update = new Update();
        update.push("loans", loan);

        this.mongoTemplate.updateFirst(query, update, BankAccount.class);

        return loan;
    }

	/**
	 * Method handling loan actions
	 */
    @Override
    @Scheduled(cron = "0 0 0 ? * *")
    @Transactional
    public void loanHandler() {
		// Find site config
		List<SiteConfig> siteConfig = this.configRepository.findAll();

		// Check if exists
        if(siteConfig.size() > 0) {
			SiteConfig config = new SiteConfig();
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());

            Date lastAutoPay = null;
            Date lastCalculateInterest = null;

			// If config has already last date of auto-pay loan action
            if((lastAutoPay = siteConfig.get(0).getLastAutoPayLoan()) != null) {
                Calendar autoPay = Calendar.getInstance();
                autoPay.setTime(lastAutoPay);

				// If auto-pay wasn't done today
                if(now.get(Calendar.DAY_OF_MONTH) != autoPay.get(Calendar.DAY_OF_MONTH)) {
                    this.autoPayLoans();
					lastAutoPay = new Date();
                }
            }

			// If config has already last date of calculate interest action
            if((lastCalculateInterest = siteConfig.get(0).getLastCalculateInterest()) != null) {
                Calendar calculateInterest = Calendar.getInstance();
                calculateInterest.setTime(lastCalculateInterest);

				// If calc interest wasn't done today
                if(now.get(Calendar.DAY_OF_MONTH) != calculateInterest.get(Calendar.DAY_OF_MONTH)) {
                    this.calculateInterest();
					lastCalculateInterest = new Date();
                }
            }

			// Set today's dates and save config
			config.setLastAutoPayLoan(lastAutoPay);
			config.setLastCalculateInterest(lastCalculateInterest);
			this.configRepository.save(config);
        } else {
			// Run auto-pay and calculate interest methods
            this.autoPayLoans();
            this.calculateInterest();

			// Save config to db
			Date date = new Date();
			SiteConfig config = new SiteConfig();
			config.setLastCalculateInterest(date);
			config.setLastAutoPayLoan(date);

			this.configRepository.save(config);
        }
    }

	/**
	 * Method handling auto-payment for loans, that have auto-pay enabled
	 */
    @Override
    @Transactional
    public void autoPayLoans() {
		// Check if any active loans
		List<AccountAbleToPay> activeLoansAccounts = this.bankAccountRepository.getIDsOfAccountsWithActiveLoan();
        if(activeLoansAccounts.size() == 0) return;
		// Create bull operations objects
        BulkOperations userAccountsOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankAccount.class);
        BulkOperations userTransactionsOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Transaction.class);
		// Iterate over accounts with active loans
        for(AccountAbleToPay acc : activeLoansAccounts) {
			// Get specific account
            Query findAccount = new Query().addCriteria(Criteria.where("_id").is(acc.getId()));
            int loanId = acc.getLoanId();
            if(acc.isAbleToPay()) { // If account has enough balance to handle payment
				// Find this account and PLN value
                Update loanUpdate = new Update();
                Query findAccountWithPLN = new Query().addCriteria(Criteria.where("_id").is(acc.getId())).addCriteria(Criteria.where("currencies.currency").is("PLN"));
				// Remove needed value from account
                Update balanceUpdate = new Update();
                balanceUpdate.inc("currencies.$.amount", -acc.getToPay());
				// Pay interest
                if(acc.getInterest() > 0) {
                    loanUpdate.set("loans." + loanId + ".interest", (double) 0);
                }
				// Pay installments
				for(InstallmentsAmountWithId installment : acc.getInstallments()) {
					loanUpdate.set("loans." + loanId + ".installments." + installment.getId() + ".amountLeftToPay", (double) 0);
				}
				// Add operations to bull
				userAccountsOperations.updateOne(findAccount, loanUpdate);
				userAccountsOperations.updateOne(findAccountWithPLN, balanceUpdate);
				// Add transaction
				userTransactionsOperations.insert(
						new Transaction(
								acc.getId(),
								new Recipient(null, "Loan payment"),
								"Loan payment",
								new Currency(Currencies.PLN, acc.getToPay()),
								TransactionType.LOAN_PAYMENT
						));
            } else { // If not enough money
				// Add alert for user
                Update update = new Update().push("alertsList",
                        new Alert(
                                "Insufficient funds",
                                "Your balance is too low to auto-pay loan. Your loan auto-pay is now off."
                        )
                );
                update.set("loans." + loanId + ".autoPayment", false);
				userAccountsOperations.updateOne(findAccount, update);
            }
        }
		// Execute bull operations
		userAccountsOperations.execute();
		userTransactionsOperations.execute();
	}

	/**
	 * Method handling delayed payments and adds interest to those
	 */
    @Override
    @Transactional
    public void calculateInterest() {
		// Get accounts with their interest
		List<AccountWithInterest> accountsWithInterest = this.bankAccountRepository.getAccountsWithInterestToPay();
        if(accountsWithInterest.size() == 0) return;
		// Create bull instance
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankAccount.class);
		// Iterate over found accounts
        for(AccountWithInterest acc : accountsWithInterest) {
			// Find accounts and add interest to them
            Query findAccount = new Query().addCriteria(Criteria.where("_id").is(acc.getId()));
            Update updateInterest = new Update();
            updateInterest.inc("loans." + acc.getLoanId() + ".interest", acc.getInterest());
			// Add alert to inform user
            updateInterest.push("alertsList", new Alert(
                    "Delayed payment",
                    "Interest was applied due to delayed payment"
            ));
            bulkOperations.updateOne(findAccount, updateInterest);
        }
		// Execute bull operations
        bulkOperations.execute();
    }

	/**
	 * Method changing account's loan auto-pay
	 * @param iban iban of account
	 * @param autoPayment value to be change to
	 * @throws ThrowableErrorResponse when account has no active loan
	 */
	@Override
	@Transactional
	public void setAutoPayment(String iban, boolean autoPayment) throws ThrowableErrorResponse {
		// Get last loan id for specific account
		Optional<Integer> id = this.bankAccountRepository.getLastLoanId(iban);
		if(id.isEmpty()) {
			throw new ThrowableErrorResponse(
					"No loan found",
					"No active loan found for iban " + iban,
					404);
		}
		// Change auto-pay value
		Query query = new Query(Criteria.where("_id").is(iban));
		Update update = new Update().set("loans." + id.get() + ".autoPayment", autoPayment);
		this.mongoTemplate.updateFirst(query, update, BankAccount.class);
	}

	/**
	 * Method to pay account's loan
	 * @param iban iban of account to pay loan
	 * @param amount amount to be paid
	 * @return value left after payment
	 * @throws ThrowableErrorResponse when balance is too low
	 */
	@Override
	@Transactional
	public double payLoan(String iban, double amount) throws ThrowableErrorResponse {
		// Find account with balance and installments
		SingleAccountWithToPay acc = this.bankAccountRepository.getSingleAccountWithToPay(iban);
		if(acc.getBalance() < amount) {
			throw new ThrowableErrorResponse(
					"Balance too low",
					"Account's balance is too low to perform this operation",
					400
			);
		}
		double amountAvailable = amount;

		// Find account and it's PLN balance
		Query query = new Query(Criteria.where("_id").is(iban)).addCriteria(Criteria.where("currencies.currency").is("PLN"));
		Update update = new Update();

		if(acc.getInterest() < amountAvailable) { // If amount is higher than interest to pay
			update.set("loans." + acc.getLoanId() + ".interest", (double)0);
			amountAvailable -= acc.getInterest();
		} else { // If amount is lower or equal to interest to pay
			update.inc("loans." + acc.getLoanId() + ".interest", -amountAvailable);
			update.inc("currencies.$.amount", -amount);
			this.mongoTemplate.updateFirst(query, update, BankAccount.class);
			return 0;
		}

		// Iterate over installments
		for(InstallmentsAmountWithId inst : acc.getInstallments()) {
			if(inst.getAmountLeftToPay() < amountAvailable) { // If amount is higher than installment to pay
				update.set("loans." + acc.getLoanId() + ".installments." + inst.getId() + ".amountLeftToPay", (double)0);
				amountAvailable -= inst.getAmountLeftToPay();
			} else { // If amount is lower or equal to installment to pay
				update.inc("loans." + acc.getLoanId() + ".installments." + inst.getId() + ".amountLeftToPay", -amountAvailable);
				update.inc("currencies.$.amount", -amount);
				this.mongoTemplate.updateFirst(query, update, BankAccount.class);
				return 0;
			}
		}

		// Take amount from user's balance
		update.inc("currencies.$.amount", -(amount - amountAvailable));
		this.mongoTemplate.updateFirst(query, update, BankAccount.class);

		// Round result
		BigDecimal roundedAvailable = new BigDecimal(Double.toString(amountAvailable));
		roundedAvailable = roundedAvailable.setScale(2, RoundingMode.HALF_UP);
		return roundedAvailable.doubleValue();
	}
}
