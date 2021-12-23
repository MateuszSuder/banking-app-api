package banking.application.model;

import banking.application.util.Currencies;

/**
 * Class defining currency
 */
public class Currency {
    Currencies currency;
    double amount;

    public Currency(Currencies currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currencies getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}

