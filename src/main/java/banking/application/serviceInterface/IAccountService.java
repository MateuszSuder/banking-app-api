package banking.application.serviceInterface;

import banking.application.model.Currency;
import banking.application.model.User;
import banking.application.util.AccountType;
import banking.application.util.Currencies;
import banking.application.util.IBAN;

import java.util.List;

public interface IAccountService {
    IBAN openAccount(User user, AccountType ac);
    List<Currency> getAccountBalances(String iban, List<Currencies> currencies);
    double transferMoney(String from, String to, Currency currency);
}
