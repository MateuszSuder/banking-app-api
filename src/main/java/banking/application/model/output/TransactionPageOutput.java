package banking.application.model.output;

import banking.application.model.Transaction;

import java.util.List;

/**
 * Model for return data for transaction page request
 */
public class TransactionPageOutput {
    List<Transaction> transactions;
    PaginationOutput paginationOutput;

    public TransactionPageOutput(List<Transaction> transactions, PaginationOutput paginationOutput) {
        this.transactions = transactions;
        this.paginationOutput = paginationOutput;
    }

    public PaginationOutput getPaginationOutput() {
        return paginationOutput;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
