package banking.application.model;

import banking.application.util.Currencies;
import banking.application.util.TransactionType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document for transactions collection
 */
@Document("transactions")
public class Transaction {

    @Indexed
    String from;
    Recipient receiverInfo;
    @Indexed
    String title;
    Currency sendValue;
    @Indexed
    TransactionType transactionType;

    public Transaction(String from, Recipient receiverInfo, String title, Currency sendValue, TransactionType transactionType) {
        this.from = from;
        this.receiverInfo = receiverInfo;
        this.title = title;
        this.sendValue = sendValue;
        this.transactionType = transactionType;
    }

    public String getFrom() {
        return from;
    }

    public Recipient getReceiverInfo() {
        return receiverInfo;
    }

    public String getTitle() {
        return title;
    }

    public Currency getSendValue() {
        return sendValue;
    }

    public String getAccountNumber() {
        return receiverInfo.getAccountNumber();
    }
    public String getRecipientName() {
        return receiverInfo.getRecipientName();
    }
    public Currencies getCurrency() {
        return sendValue.getCurrency();
    }
    public double getAmount() {
        return sendValue.getAmount();
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
}
