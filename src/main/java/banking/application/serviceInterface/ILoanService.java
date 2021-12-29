package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Loan;
import banking.application.model.input.LoanInput;

public interface ILoanService {
    boolean accountHasActiveLoan(String iban);
    Loan takeLoan(String iban, LoanInput loanInput) throws ThrowableErrorResponse;
}
