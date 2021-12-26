package banking.application.model.input;

import banking.application.model.Currency;
import banking.application.model.Recipient;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransferInput {
    @NotNull(message = "Recipient is missing")
    @Valid Recipient to;

    @NotNull(message = "Value is missing")
    @Valid Currency value;

    @NotBlank(message = "Title is missing")
    String title;

    public Recipient getTo() {
        return to;
    }

    public Currency getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}
