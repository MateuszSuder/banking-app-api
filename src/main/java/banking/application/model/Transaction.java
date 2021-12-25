package banking.application.model;

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

    public Transaction(String from, Recipient receiverInfo, String title, Currency sendValue) {
        this.from = from;
        this.receiverInfo = receiverInfo;
        this.title = title;
        this.sendValue = sendValue;
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
