package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Currency;
import banking.application.model.Recipient;
import banking.application.model.User;
import banking.application.util.AccountType;
import banking.application.util.Currencies;
import banking.application.util.IBAN;
import banking.application.util.TransactionType;

import java.util.List;

public interface IAccountService {
    IBAN openAccount(User user, AccountType ac);
    List<Currency> getAccountBalances(String iban, List<Currencies> currencies);
    Currency transferMoney(String from, Recipient to, Currency currency, String title, TransactionType transactionType) throws ThrowableErrorResponse;
}
