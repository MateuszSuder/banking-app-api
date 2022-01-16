package banking.application.model;

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

	public ExchangeInfo(Currency from, Currency to, Double rate) {
		this.from = from;
		this.to = to;
		this.rate = rate;
	}

	public Currency getFrom() {
		return from;
	}

	public Currency getTo() {
		return to;
	}

	public Double getRate() {
		return rate;
	}

	@Nullable
	public Double getFee() {
		return fee;
	}
}
