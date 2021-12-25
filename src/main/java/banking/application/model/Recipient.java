package banking.application.model;

import javax.validation.constraints.NotBlank;

/**
 * Simple class defining recipient
 */
public class Recipient {
    @NotBlank(message = "Account number is missing")
    String accountNumber; // IBAN
    @NotBlank(message = "Recipient name is missing")
    String recipientName;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }
}
