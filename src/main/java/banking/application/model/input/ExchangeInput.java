package banking.application.model.input;

import banking.application.model.Currency;
import banking.application.util.Currencies;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Model input for exchange info
 */
public class ExchangeInput {
	@NotNull(message = "From is missing")
	@Valid private Currency from;

	@NotNull(message = "To is missing")
	private Currencies to;

	public Currency getFrom() {
		return from;
	}

	public Currencies getTo() {
		return to;
	}
}
