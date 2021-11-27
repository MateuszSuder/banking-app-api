package banking.application.routes.Account.BankAccount;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("bankaccounts")
public class BankAccount {

    @Id
    private String id;

    private List<Currency> currencies;
    private List<Code> codes;

    public BankAccount(String id, List<Currency> currencies, List<Code> codes) {
        this.id = id;
        this.currencies = currencies;
        this.codes = codes;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id='" + id + '\'' +
                ", currencies=" + currencies +
                ", codes=" + codes +
                '}';
    }
}

