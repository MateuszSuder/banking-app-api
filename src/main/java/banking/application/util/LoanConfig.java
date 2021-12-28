package banking.application.util;

public final class LoanConfig {
    public double getLoanRate() {
        return 1.75;
    }

    public int getMinLoanLengthInMonths() {
        return 3;
    }

    public int getMaxLoanLengthInMonths() {
        return 60;
    }

    public double getMinLoanValue() {
        return 1000;
    }

    public double getMaxLoanValue() {
        return 25000;
    }
}
