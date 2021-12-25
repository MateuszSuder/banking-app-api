package banking.application.serviceInterface;

import banking.application.model.Transaction;
import banking.application.model.input.TransactionPageInput;

import java.util.List;

public interface ITransactionService {
    List<Transaction> getTransactionsPage(String ownerIBAN, TransactionPageInput transactionPageInput);
}
