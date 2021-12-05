package banking.application.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("exchangeRates")
public class ExchangeRate {
    private CurrencyType type;
    private List<Rate> rates;
    private String defaultCurrency;
}

