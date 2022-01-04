package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Loan;
import banking.application.model.input.LoanInput;

public interface ILoanService {
    boolean accountHasActiveLoan(String iban);
    Loan takeLoan(String iban, LoanInput loanInput) throws ThrowableErrorResponse;
    void loanHandler();
    void autoPayLoans();
    void calculateInterest();
    void setAutoPayment(String iban, boolean autoPayment) throws ThrowableErrorResponse;
    double payLoan(String iban, double amount) throws ThrowableErrorResponse;
}
