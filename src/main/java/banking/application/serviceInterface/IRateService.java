package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Currency;
import banking.application.model.ExchangeInfo;
import banking.application.model.ExchangePair;
import banking.application.model.ExchangeRate;
import banking.application.util.Currencies;

import java.util.Collection;

public interface IRateService {
	Collection<ExchangeRate> getRates();
	ExchangeRate getRate(Currencies currency) throws ThrowableErrorResponse;
	ExchangePair getPair(Currencies defaultCurrency, Currencies currencyToBePaired) throws ThrowableErrorResponse;
	ExchangeInfo exchangeInfo(Currency from, Currencies to) throws ThrowableErrorResponse;
	ExchangeInfo exchange(String iban, boolean newCurrency, ExchangeInfo info) throws ThrowableErrorResponse;
}
