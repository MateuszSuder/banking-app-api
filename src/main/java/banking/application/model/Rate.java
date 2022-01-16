package banking.application.model;

import banking.application.util.Currencies;
import org.springframework.lang.Nullable;

/**
 * Class showing Rate model
 */
public class Rate {
    Currencies currency;
    double price;
    @Nullable
    Float withdrawFee;

    public Rate(Currencies currency, double price) {
        this.currency = currency;
        this.price = price;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public double getPrice() {
        return price;
    }
}
