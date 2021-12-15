package banking.application.service;

import banking.application.model.BankAccount;
import banking.application.model.Currency;
import banking.application.model.User;
import banking.application.serviceInterface.IAccountService;
import banking.application.util.AccountType;
import banking.application.util.IBAN;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService extends EntryService implements IAccountService {
    /**
     * Method creating user account
     * @param user Auth0 user
     * @param ac Account type to create
     * @return iban
     */
    @Override
    public IBAN openAccount(User user, AccountType ac) {
        // Create iban
        IBAN iban = new IBAN(ac, user.getUser_id());

        // Create account and assign iban and codes to it. Users will get 10000zł for testing purposes
        BankAccount account = new BankAccount(iban.getIBAN(), List.of(new Currency("PLN", 10000F)));

        // Save account to database
        this.bankAccountRepository.save(account);

        return iban;
    }
}

