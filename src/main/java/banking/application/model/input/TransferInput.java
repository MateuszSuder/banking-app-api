package banking.application.model.input;

import banking.application.model.Currency;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransferInput {
    @NotBlank(message = "Recipient IBAN ('to' field) is missing")
    String to;

    @NotNull(message = "Value is missing")
    @Valid Currency value;

    public String getTo() {
        return to;
    }

    public Currency getValue() {
        return value;
    }
}
