package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.model.input.LoanInput;
import banking.application.serviceInterface.ILoanService;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class LoanService extends EntryService implements ILoanService {
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
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());

            Date lastAutoPay = null;
            Date lastCalculateInterest = null;

            if((lastAutoPay = siteConfig.get(0).getLastAutoPayLoan()) != null) {
                Calendar autoPay = Calendar.getInstance();
                autoPay.setTime(lastAutoPay);

                if(now.get(Calendar.DAY_OF_MONTH) != autoPay.get(Calendar.DAY_OF_MONTH)) {
                    this.autoPayLoans();
                }
            }

            if((lastCalculateInterest = siteConfig.get(0).getLastCalculateInterest()) != null) {
                Calendar calculateInterest = Calendar.getInstance();
                calculateInterest.setTime(lastCalculateInterest);

                if(now.get(Calendar.DAY_OF_MONTH) != calculateInterest.get(Calendar.DAY_OF_MONTH)) {
                    this.calculateInterest();
                }
            }

        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 * * ? * *")
    public void autoPayLoans() {
        List<AccountAbleToPay> activeLoansAccounts = this.bankAccountRepository.getIDsOfAccountsWithActiveLoan();
        if(activeLoansAccounts.size() == 0) return;
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankAccount.class);
        for(AccountAbleToPay acc : activeLoansAccounts) {
            Query findAccount = new Query().addCriteria(Criteria.where("_id").is(acc.getId()));
            int loanId = acc.getLoanId();
            if(acc.isAbleToPay()) {
                Update loanUpdate = new Update();
                Query findAccountWithPLN = new Query().addCriteria(Criteria.where("_id").is(acc.getId())).addCriteria(Criteria.where("currencies.currency").is("PLN"));
                Update balanceUpdate = new Update();
                balanceUpdate.inc("currencies.$.amount", -acc.getToPay());
                if(acc.getInterest() > 0) {
                    loanUpdate.set("loans." + loanId + ".interest", 0);
                    for(InstallmentsAmountWithId installment : acc.getInstallments()) {
                        loanUpdate.set("loans." + loanId + ".installments." + installment.getId() + ".amountLeftToPay", 0);
                    }
                }
                bulkOperations.updateOne(findAccount, loanUpdate);
                bulkOperations.updateOne(findAccountWithPLN, balanceUpdate);
            } else {
                Update update = new Update().push("alertsList",
                        new Alert(
                                "Insufficient funds",
                                "Your balance is too low to auto-pay loan. Your loan auto-pay is now off."
                        )
                );
                update.set("loans." + loanId + ".autoPayment", false);
                bulkOperations.updateOne(findAccount, update);
            }
        }
        BulkWriteResult bulkWriteResult = bulkOperations.execute();
    }

    @Override
    @Transactional
    public void calculateInterest() {

    }
}
