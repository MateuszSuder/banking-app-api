package banking.application.model.input;

import banking.application.util.LoanConfig;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

public class LoanInput {
    @Min(value = LoanConfig.minLoanValue, message = "Loan value must be ≥ " + LoanConfig.minLoanValue)
    @Max(value = LoanConfig.maxLoanValue, message = "Loan value must be ≤ " + LoanConfig.maxLoanValue)
    float loanAmount;

    @Min(value = LoanConfig.minLoanLengthInMonths, message = "Loan length in months must be ≥ " + LoanConfig.minLoanLengthInMonths)
    @Max(value = LoanConfig.maxLoanLengthInMonths, message = "Loan length in months must be ≤ " + LoanConfig.maxLoanLengthInMonths)
    int loanLength;

    @Positive(message = "Loan rate is missing or invalid")
    double loanRate;

    @Nullable
    Boolean autoPayment;

    public LoanInput(float loanAmount, int loanLength, double loanRate, @Nullable Boolean autoPayment) {
        this.loanAmount = loanAmount;
        this.loanLength = loanLength;
        this.loanRate = loanRate;
        this.autoPayment = autoPayment;
    }

    public float getLoanAmount() {
        return loanAmount;
    }

    public int getLoanLength() {
        return loanLength;
    }

    public double getLoanRate() {
        return loanRate;
    }

    @Nullable
    public Boolean getAutoPayment() {
        return autoPayment;
    }
}
