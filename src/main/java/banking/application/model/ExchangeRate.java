package banking.application.model;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.util.Currencies;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Document for exchangeRates collection
 */
@Document("exchangeRates")
public class ExchangeRate {
    private CurrencyType type;
    private List<Rate> rates;
    private Currencies defaultCurrency;

    public ExchangeRate(CurrencyType type, List<Rate> rates, Currencies defaultCurrency) {
        this.type = type;
        this.rates = rates;
        this.defaultCurrency = defaultCurrency;
    }

    /**
     * Method finding and creating exchange pair
     * @param toPair currency to be paired
     * @return exchange pair
     * @throws ThrowableErrorResponse for invalid currency
     */
    public ExchangePair getPair(Currencies toPair) throws ThrowableErrorResponse {
        Rate rate = rates.stream()
                .filter(r -> r.getCurrency().equals(toPair))
                .findAny()
                .orElse(null);
        if(rate == null) {
            throw new ThrowableErrorResponse(
                    "Not found",
                    "Currency " + toPair + " not found",
                    404);
        }

        return new ExchangePair(
                this.type,
                rate,
                this.defaultCurrency
        );
    }

    public CurrencyType getType() {
        return type;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public Currencies getDefaultCurrency() {
        return defaultCurrency;
    }
}

