package banking.application.util;

import banking.application.model.CurrencyType;
import banking.application.model.ExchangeRate;
import banking.application.model.Rate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Util class containing exchange rates
 */
public class ExchangeRateConfig {
	private final HashMap<Currencies, ExchangeRate> exchangeRates = new HashMap<Currencies, ExchangeRate>();

	public ExchangeRateConfig() {
		this.saveRate(CurrencyType.fiat, Currencies.PLN,
				new Rate(Currencies.USD, 4.04),
				new Rate(Currencies.CHF, 4.41),
				new Rate(Currencies.EUR, 4.56),
				new Rate(Currencies.JPY, 0.035),
				new Rate(Currencies.GBP, 5.47)
		);

		this.saveRate(CurrencyType.fiat, Currencies.USD,
				new Rate(Currencies.PLN, 0.25),
				new Rate(Currencies.CHF, 1.09),
				new Rate(Currencies.EUR, 1.13),
				new Rate(Currencies.JPY, 0.0086),
				new Rate(Currencies.GBP, 1.35)
		);

		this.saveRate(CurrencyType.fiat, Currencies.CHF,
				new Rate(Currencies.PLN, 0.23),
				new Rate(Currencies.USD, 0.92),
				new Rate(Currencies.EUR, 1.03),
				new Rate(Currencies.JPY, 0.0079),
				new Rate(Currencies.GBP, 1.24)
		);

		this.saveRate(CurrencyType.fiat, Currencies.EUR,
				new Rate(Currencies.PLN, 0.22),
				new Rate(Currencies.USD, 0.89),
				new Rate(Currencies.CHF, 0.97),
				new Rate(Currencies.JPY, 0.0076),
				new Rate(Currencies.GBP, 1.20)
		);
		this.saveRate(CurrencyType.fiat, Currencies.JPY,
				new Rate(Currencies.PLN, 28.73),
				new Rate(Currencies.USD, 116.14),
				new Rate(Currencies.CHF, 126.75),
				new Rate(Currencies.EUR, 131.09),
				new Rate(Currencies.GBP, 157.13)
		);

		this.saveRate(CurrencyType.fiat, Currencies.GBP,
				new Rate(Currencies.PLN, 0.13),
				new Rate(Currencies.USD, 0.74),
				new Rate(Currencies.CHF, 0.81),
				new Rate(Currencies.EUR, 0.83),
				new Rate(Currencies.JPY, 0.0064)
		);

		this.saveRate(CurrencyType.crypto, Currencies.USDT,
				new Rate(Currencies.BTC, 46112.10),
				new Rate(Currencies.ETH, 3819.53)
		);

		this.saveRate(CurrencyType.crypto, Currencies.BTC,
				new Rate(Currencies.USDT, 0.000022),
				new Rate(Currencies.ETH, 0.081013)
		);

		this.saveRate(CurrencyType.crypto, Currencies.ETH,
				new Rate(Currencies.USDT, 0.00026221),
				new Rate(Currencies.BTC, 12.091260)
		);
	}

	/**
	 * Upsert rate
	 * @param type type of currency - FIAT/CRYPTO
	 * @param defaultCurrency right side of exchange rate ex. PLN/USD - USD is defaultCurrency
	 * @param rates list of rates for current default currency
	 */
	public void saveRate(CurrencyType type, Currencies defaultCurrency, Rate ...rates) {
		ExchangeRate rate = new ExchangeRate(type, List.of(rates), defaultCurrency);
		this.exchangeRates.put(defaultCurrency, rate);
	}

	/**
	 * Get saved rate
	 * @param currency currency to get rates of
	 * @return exchange rate for given currency
	 * @throws IllegalArgumentException for not found currency
	 */
	public ExchangeRate getRate(Currencies currency) throws IllegalArgumentException {
		if(!this.exchangeRates.containsKey(currency)) {
			throw new IllegalArgumentException("Provided currency doesn't exist");
		}

		return this.exchangeRates.get(currency);
	}

	/**
	 * Get all rates
	 * @return saved exchange rates
	 */
	public Collection<ExchangeRate> getRates() {
		return this.exchangeRates.values();
	}
}
