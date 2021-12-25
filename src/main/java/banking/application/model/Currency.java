package banking.application.model;

import banking.application.util.Currencies;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Class defining currency
 */
public class Currency {

    @NotNull(message = "Currency is missing")
    Currencies currency;

    @Positive(message = "Amount must be higher than 0")
    double amount;

    public Currency(Currencies currency, double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "currency='" + currency + '\'' +
                ", amount=" + amount +
                '}';
    }
}

