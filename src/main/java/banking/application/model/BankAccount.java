package banking.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Document for bank accounts
 */
@Document("bankAccounts")
public class BankAccount {

    @Id
    private String id;

    private List<Currency> currencies;
    private List<Code> codes;

    @Nullable
    private List<Loan> loans;

    @Nullable
    private List<StandingOrder> standingOrders;

    @Nullable
    private List<SavedRecipients> savedRecipients;

    @Nullable // Nullable for every but crypto account
    private CryptoAccountInfo cryptoAccountInfo;


    public BankAccount(String id, List<Currency> currencies, List<Code> codes) {
        this.id = id;
        this.currencies = currencies;
        this.codes = codes;
    }

    public List<Code> getCodes() {
        return codes;
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

