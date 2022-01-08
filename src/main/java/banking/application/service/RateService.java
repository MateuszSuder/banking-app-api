package banking.application.service;

import banking.application.model.Currency;
import banking.application.model.ExchangeInfo;
import banking.application.model.ExchangePair;
import banking.application.model.ExchangeRate;
import banking.application.serviceInterface.IRateService;
import banking.application.util.Currencies;
import banking.application.util.ExchangeRateConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RateService extends EntryService implements IRateService {
	private final ExchangeRateConfig exchangeRateConfig = new ExchangeRateConfig();

	/**
	 * Get list of exchange rates
	 * @return exchange rates
	 */
	@Override
	public Collection<ExchangeRate> getRates() {
		return this.exchangeRateConfig.getRates();
	}

	/**
	 * Get single exchange rate
	 * @param currency currency of which exchange rate to get
	 * @return exchange rate of currency given in argument
	 */
	@Override
	public ExchangeRate getRate(Currencies currency) {
		return null;
	}

	/**
	 * Get pair for two given currencies
	 * @param defaultCurrency input currency
	 * @param currencyToBePaired output currency
	 * @return pair of two currencies given in arguments
	 */
	@Override
	public ExchangePair getPair(Currencies defaultCurrency, Currencies currencyToBePaired) {
		return null;
	}

	/**
	 * Get info about exchange rate
	 * @param from currency from which to exchange
	 * @param to currency to which to exchange
	 * @return info about asked exchange
	 */
	@Override
	public ExchangeInfo exchangeInfo(Currency from, Currencies to) {
		return null;
	}
}
