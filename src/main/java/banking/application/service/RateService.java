package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.serviceInterface.IRateService;
import banking.application.util.Currencies;
import banking.application.util.ExchangeRateConfig;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
	public ExchangeRate getRate(Currencies currency) throws ThrowableErrorResponse {
		try {
			return this.exchangeRateConfig.getRate(currency);
		} catch (IllegalArgumentException e) {
			throw new ThrowableErrorResponse(
					"Invalid argument",
					e.getMessage(),
					404);
		}
	}

	/**
	 * Get pair for two given currencies
	 * @param defaultCurrency input currency
	 * @param currencyToBePaired output currency
	 * @throws ThrowableErrorResponse for invalid currency
	 * @return pair of two currencies given in arguments
	 */
	@Override
	public ExchangePair getPair(Currencies defaultCurrency, Currencies currencyToBePaired) throws ThrowableErrorResponse {
		ExchangeRate exchangeRate = this.exchangeRateConfig.getRate(defaultCurrency);
		return exchangeRate.getPair(currencyToBePaired);
	}

	/**
	 * Get info about exchange rate
	 * @param from currency from which to exchange
	 * @param to currency to which to exchange
	 * @return info about asked exchange
	 */
	@Override
	public ExchangeInfo exchangeInfo(Currency from, Currencies to) throws ThrowableErrorResponse {
		ExchangePair exchangePair = this.exchangeRateConfig.getRate(from.getCurrency()).getPair(to);
		double rate = exchangePair.getRate().getPrice();
		BigDecimal roundedExchanged = new BigDecimal(Double.toString(from.getAmount() / rate));
		roundedExchanged = roundedExchanged.setScale(2, RoundingMode.FLOOR);
		Currency toCurrency = new Currency(to, roundedExchanged.doubleValue());
		return new ExchangeInfo(from, toCurrency, rate);
	}

	/**
	 * Exchange given currency
	 * @param iban iban of account
	 * @param newCurrency if currency doesn't exist for account
	 * @param info exchange info
	 * @return exchange info
	 */
	@Transactional
	@Override
	public ExchangeInfo exchange(String iban, boolean newCurrency, ExchangeInfo info) {
		Query query = new Query(Criteria.where("_id").is(iban)).addCriteria(Criteria.where("currencies.currency").is(info.getFrom().getCurrency()));
		Update update = new Update().inc("currencies.$.amount", -info.getFrom().getAmount());

		Query query2 = new Query();
		Update update2 = new Update();

		if(newCurrency) {
			query2.addCriteria(Criteria.where("_id").is(iban));
			update2.push("currencies", new Currency(info.getTo().getCurrency(), info.getTo().getAmount()));
		} else {
			query2.addCriteria(Criteria.where("_id").is(iban)).addCriteria(Criteria.where("currencies.currency").is(info.getTo().getCurrency()));
			update2.inc("currencies.$.amount", info.getTo().getAmount());
		}

		this.mongoTemplate.updateFirst(query, update, BankAccount.class);
		this.mongoTemplate.updateFirst(query2, update2, BankAccount.class);

		return info;
	}
}
