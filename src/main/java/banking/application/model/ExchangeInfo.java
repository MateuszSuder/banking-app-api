package banking.application.model;

import banking.application.util.Currencies;
import org.springframework.lang.Nullable;

/**
 * Class containing info about exchange
 */
public class ExchangeInfo {
	private Currency from;
	private Currency to;
	private Double rate;
	@Nullable
	private Double fee;
}
