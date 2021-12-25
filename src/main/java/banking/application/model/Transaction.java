package banking.application.model;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document for transactions collection
 */
@Document("transactions")
public class Transaction {
    private final String from;
    private final Recipient receiverInfo;
    private final String title;
    private final Currency sendValue;

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
