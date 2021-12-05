package banking.application.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("transactions")
public class Transaction {
    private String from;
    private Recipient receiverInfo;
    private String title;
    private Currency sendValue;
}
