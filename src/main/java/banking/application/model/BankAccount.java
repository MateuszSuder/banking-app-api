package banking.application.model;

import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * Document for bank accounts
 */
@Document("bankAccounts")
public class BankAccount {

    @Id
    private String id;

    private List<Currency> currencies;

    @Nullable
    private List<Loan> loans;

    @Nullable
    private List<StandingOrder> standingOrders;

    @Nullable
    private List<Recipient> savedRecipients;

    @Nullable // Nullable for every but crypto account
    private CryptoAccountInfo cryptoAccountInfo;

    @Nullable
    private List<Alert> alertsList;

    public BankAccount(String id, List<Currency> currencies) {
        this.id = id;
        this.currencies = currencies;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public List<Loan> getLoans() {
        return loans;
    }

    @Nullable
    public List<StandingOrder> getStandingOrders() {
        return standingOrders;
    }

    @Nullable
    public List<Recipient> getSavedRecipients() {
        return savedRecipients;
    }

    @Nullable
    public CryptoAccountInfo getCryptoAccountInfo() {
        return cryptoAccountInfo;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    @Nullable
    public List<Alert> getAlertsList() {
        return alertsList;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id='" + id + '\'' +
                ", currencies=" + currencies +
                '}';
    }
}

