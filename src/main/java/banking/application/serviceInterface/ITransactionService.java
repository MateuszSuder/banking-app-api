package banking.application.serviceInterface;

import banking.application.model.input.TransactionPageInput;
import banking.application.model.output.TransactionPageOutput;

public interface ITransactionService {
    TransactionPageOutput getTransactionsPage(String ownerIBAN, TransactionPageInput transactionPageInput);
}
