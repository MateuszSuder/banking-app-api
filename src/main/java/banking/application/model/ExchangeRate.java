package banking.application.model;

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
}

