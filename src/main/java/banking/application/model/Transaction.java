package banking.application.model;

import banking.application.util.TransactionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Document for transactions collection
 */
@Document("transactions")
public class Transaction {

    @Indexed(name = "fromIBAN")
    String from;
    Recipient receiverInfo;
    String title;
    Currency sendValue;
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
}
