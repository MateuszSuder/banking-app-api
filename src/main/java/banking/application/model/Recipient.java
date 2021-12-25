package banking.application.model;

import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;

/**
 * Simple class defining recipient
 */
public class Recipient {
    @Indexed
    @NotBlank(message = "Account number is missing")
    String accountNumber; // IBAN
    @Indexed
    @NotBlank(message = "Recipient name is missing")
    String recipientName;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
