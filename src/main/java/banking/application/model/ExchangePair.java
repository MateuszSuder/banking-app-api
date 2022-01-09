package banking.application.model;

import banking.application.util.Currencies;

/**
 * Model containing information about two specific currencies
 */
public class ExchangePair {
	private CurrencyType type;
	private Rate rate;
	private Currencies defaultCurrency;

	public ExchangePair(CurrencyType type, Rate rate, Currencies defaultCurrency) {
		this.type = type;
		this.rate = rate;
		this.defaultCurrency = defaultCurrency;
	}

	public Rate getRate() {
		return rate;
	}
}
