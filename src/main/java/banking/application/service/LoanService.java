package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.BankAccount;
import banking.application.model.Installment;
import banking.application.model.Loan;
import banking.application.model.input.LoanInput;
import banking.application.serviceInterface.ILoanService;
import banking.application.util.LoanConfig;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@Service
public class LoanService extends EntryService implements ILoanService {
    @Override
    public boolean accountHasActiveLoan(String iban) {
        return false;
    }

    @Override
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

        this.mongoTemplate.updateMulti(query, update, BankAccount.class);

        return loan;
    }
}
