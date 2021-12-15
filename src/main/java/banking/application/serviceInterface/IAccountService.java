package banking.application.serviceInterface;

import banking.application.model.User;
import banking.application.util.AccountType;
import banking.application.util.IBAN;

public interface IAccountService {
    IBAN openAccount(User user, AccountType ac);
}
