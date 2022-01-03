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

@Service
public class LoanService extends EntryService implements ILoanService {

	@Bean(initMethod = "init")
	public void init() {
		this.loanHandler();
	}

    @Override
    public boolean accountHasActiveLoan(String iban) {
        Optional<Boolean> active = this.bankAccountRepository.checkIfAccountIsActive(iban);

        return active.orElse(false);
    }

    @Override
    @Transactional
    public Loan takeLoan(String iban, LoanInput loanInput) throws ThrowableErrorResponse {
        if(this.accountHasActiveLoan(iban)) {
            throw new ThrowableErrorResponse(
                    "Account has active loan",
                    "Account can have only one active loan at the time",
                    409);
        }

        double loanAmountWithInterest = loanInput.getLoanAmount() + loanInput.getLoanAmount() * (loanInput.getLoanRate() / 100);
        double loanAmountPerMonth = loanAmountWithInterest / loanInput.getLoanLength();

        BigDecimal roundedPerMonth = new BigDecimal(Double.toString(loanAmountPerMonth));
        roundedPerMonth = roundedPerMonth.setScale(2, RoundingMode.HALF_UP);

        ArrayList<Installment> installments = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
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

        cal = Calendar.getInstance();
        Date startedAt = cal.getTime();
        cal.add(Calendar.MONTH, loanInput.getLoanLength());
        Date endsAt = cal.getTime();

        Loan loan = new Loan(
                startedAt,
                endsAt,
                loanInput.getLoanAmount(),
                loanAmountWithInterest,
                installments,
                0,
                Boolean.TRUE.equals(loanInput.getAutoPayment())
        );

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(iban));
        Update update = new Update();
        update.push("loans", loan);

        this.mongoTemplate.updateFirst(query, update, BankAccount.class);

        return loan;
    }

    @Override
    @Scheduled(cron = "0 0 0 ? * *")
    @Transactional
    public void loanHandler() {
		List<SiteConfig> siteConfig = this.configRepository.findAll();

        if(siteConfig.size() > 0) {
			SiteConfig config = new SiteConfig();
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());

            Date lastAutoPay = null;
            Date lastCalculateInterest = null;

            if((lastAutoPay = siteConfig.get(0).getLastAutoPayLoan()) != null) {
                Calendar autoPay = Calendar.getInstance();
                autoPay.setTime(lastAutoPay);

                if(now.get(Calendar.DAY_OF_MONTH) != autoPay.get(Calendar.DAY_OF_MONTH)) {
                    this.autoPayLoans();
					lastAutoPay = new Date();
                }
            }

            if((lastCalculateInterest = siteConfig.get(0).getLastCalculateInterest()) != null) {
                Calendar calculateInterest = Calendar.getInstance();
                calculateInterest.setTime(lastCalculateInterest);

                if(now.get(Calendar.DAY_OF_MONTH) != calculateInterest.get(Calendar.DAY_OF_MONTH)) {
                    this.calculateInterest();
					lastCalculateInterest = new Date();
                }
            }

			config.setLastAutoPayLoan(lastAutoPay);
			config.setLastCalculateInterest(lastCalculateInterest);
			this.configRepository.save(config);
        } else {
            this.autoPayLoans();
            this.calculateInterest();

			Date date = new Date();
			SiteConfig config = new SiteConfig();
			config.setLastCalculateInterest(date);
			config.setLastAutoPayLoan(date);

			this.configRepository.save(config);
        }
    }

    @Override
    @Transactional
    public void autoPayLoans() {
		List<AccountAbleToPay> activeLoansAccounts = this.bankAccountRepository.getIDsOfAccountsWithActiveLoan();
        if(activeLoansAccounts.size() == 0) return;
        BulkOperations userAccountsOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankAccount.class);
        BulkOperations userTransactionsOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Transaction.class);
        for(AccountAbleToPay acc : activeLoansAccounts) {
            Query findAccount = new Query().addCriteria(Criteria.where("_id").is(acc.getId()));
            int loanId = acc.getLoanId();
            if(acc.isAbleToPay()) {
                Update loanUpdate = new Update();
                Query findAccountWithPLN = new Query().addCriteria(Criteria.where("_id").is(acc.getId())).addCriteria(Criteria.where("currencies.currency").is("PLN"));
                Update balanceUpdate = new Update();
                balanceUpdate.inc("currencies.$.amount", -acc.getToPay());
                if(acc.getInterest() > 0) {
                    loanUpdate.set("loans." + loanId + ".interest", (double) 0);
                }
				for(InstallmentsAmountWithId installment : acc.getInstallments()) {
					loanUpdate.set("loans." + loanId + ".installments." + installment.getId() + ".amountLeftToPay", (double) 0);
				}
				userAccountsOperations.updateOne(findAccount, loanUpdate);
				userAccountsOperations.updateOne(findAccountWithPLN, balanceUpdate);
				userTransactionsOperations.insert(
						new Transaction(
								acc.getId(),
								new Recipient(null, "Loan payment"),
								"Loan payment",
								new Currency(Currencies.PLN, acc.getToPay()),
								TransactionType.LOAN_PAYMENT
						));
            } else {
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
		userAccountsOperations.execute();
		userTransactionsOperations.execute();
	}

    @Override
    @Transactional
    public void calculateInterest() {
		List<AccountWithInterest> accountsWithInterest = this.bankAccountRepository.getAccountsWithInterestToPay();
        if(accountsWithInterest.size() == 0) return;
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankAccount.class);
        for(AccountWithInterest acc : accountsWithInterest) {
            Query findAccount = new Query().addCriteria(Criteria.where("_id").is(acc.getId()));
            Update updateInterest = new Update();
            updateInterest.inc("loans." + acc.getLoanId() + ".interest", acc.getInterest());
            updateInterest.push("alertsList", new Alert(
                    "Delayed payment",
                    "Interest was applied due to delayed payment"
            ));
            bulkOperations.updateOne(findAccount, updateInterest);
        }
        bulkOperations.execute();
    }

	@Override
	@Transactional
	public void setAutoPayment(String iban, boolean autoPayment) throws ThrowableErrorResponse {
		Optional<Integer> id = this.bankAccountRepository.getLastLoanId(iban);
		if(id.isEmpty()) {
			throw new ThrowableErrorResponse(
					"No loan found",
					"No active loan found for iban " + iban,
					404);
		}
		Query query = new Query(Criteria.where("_id").is(iban));
		Update update = new Update().set("loans." + id.get() + ".autoPayment", autoPayment);
		this.mongoTemplate.updateFirst(query, update, BankAccount.class);
	}

	@Override
	@Transactional
	public double payLoan(String iban, double amount) throws ThrowableErrorResponse {
		SingleAccountWithToPay acc = this.bankAccountRepository.getSingleAccountWithToPay(iban);
		if(acc.getBalance() < amount) {
			throw new ThrowableErrorResponse(
					"Balance too low",
					"Account's balance is too low to perform this operation",
					400
			);
		}
		double amountAvailable = amount;

		Query query = new Query(Criteria.where("_id").is(iban)).addCriteria(Criteria.where("currencies.currency").is("PLN"));
		Update update = new Update();

		if(acc.getInterest() < amountAvailable) {
			update.set("loans." + acc.getLoanId() + ".interest", (double)0);
			amountAvailable -= acc.getInterest();
		} else {
			update.inc("loans." + acc.getLoanId() + ".interest", -amountAvailable);
			update.inc("currencies.$.amount", -amount);
			this.mongoTemplate.updateFirst(query, update, BankAccount.class);
			return 0;
		}

		for(InstallmentsAmountWithId inst : acc.getInstallments()) {
			if(inst.getAmountLeftToPay() < amountAvailable) {
				update.set("loans." + acc.getLoanId() + ".installments." + inst.getId() + ".amountLeftToPay", (double)0);
				amountAvailable -= inst.getAmountLeftToPay();
			} else {
				update.inc("loans." + acc.getLoanId() + ".installments." + inst.getId() + ".amountLeftToPay", -amountAvailable);
				update.inc("currencies.$.amount", -amount);
				this.mongoTemplate.updateFirst(query, update, BankAccount.class);
				return 0;
			}
		}

		update.inc("currencies.$.amount", -(amount - amountAvailable));
		this.mongoTemplate.updateFirst(query, update, BankAccount.class);

		BigDecimal roundedAvailable = new BigDecimal(Double.toString(amountAvailable));
		roundedAvailable = roundedAvailable.setScale(2, RoundingMode.HALF_UP);
		return roundedAvailable.doubleValue();
	}
}
