package banking.application.model.input;

import banking.application.util.LoanConfig;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class LoanInput {
    @Min(value = LoanConfig.minLoanValue, message = "Loan value must be ≥ " + LoanConfig.minLoanValue)
    @Max(value = LoanConfig.maxLoanValue, message = "Loan value must be ≤ " + LoanConfig.maxLoanValue)
    float loanAmount;

    @Min(value = LoanConfig.minLoanLengthInMonths, message = "Loan length in months must be ≥ " + LoanConfig.minLoanLengthInMonths)
    @Max(value = LoanConfig.maxLoanLengthInMonths, message = "Loan length in months must be ≤ " + LoanConfig.maxLoanLengthInMonths)
    int loanLength;

    @Nullable
    Boolean autoPayment;

    public LoanInput(){}

    public float getLoanAmount() {
        return loanAmount;
    }

    public int getLoanLength() {
        return loanLength;
    }

    @Nullable
    public Boolean getAutoPayment() {
        return autoPayment;
    }
}
