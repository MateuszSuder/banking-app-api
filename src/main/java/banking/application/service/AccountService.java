package banking.application.service;

import banking.application.model.BankAccount;
import banking.application.model.Currency;
import banking.application.model.User;
import banking.application.serviceInterface.IAccountService;
import banking.application.util.AccountType;
import banking.application.util.Currencies;
import banking.application.util.IBAN;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        BankAccount account = new BankAccount(iban.getIBAN(), List.of(new Currency(Currencies.PLN, 10000)));

        // Save account to database
        this.bankAccountRepository.save(account);

        return iban;
    }

    /**
     * Method returning account's balances
     * @param iban iban of account
     * @param currencies list of currencies to get
     * @return List of requested balances
     */
    @Override
    public List<Currency> getAccountBalances(String iban, List<Currencies> currencies) {
        // Find account
        MatchOperation findId = Aggregation.match(new Criteria("_id").is(iban));
        // Create object for each currency entry
        UnwindOperation createObjectForEachCurrency = Aggregation.unwind("currencies");
        // Find currency needed
        MatchOperation matchSearchedCurrencies = Aggregation.match(new Criteria("currencies.currency").in(currencies));
        // Eject embedded fields, exclude id
        ProjectionOperation simplifyObject = Aggregation.project()
                .andExpression("currencies.currency").as("currency")
                .andExpression("currencies.amount").as("amount")
                .andExclude("_id");

        // Create aggregation, add stages
        Aggregation aggregation = Aggregation.newAggregation(findId, createObjectForEachCurrency, matchSearchedCurrencies, simplifyObject);

        // Run aggregation
        AggregationResults<Currency> result = mongoTemplate.aggregate(aggregation, "bankAccounts", (Currency.class));

        // Return results as list
        return result.getMappedResults();
    }

    /**
     * todo
     * Method transferring money from one account to another
     * @param from from which account
     * @param to to which account
     * @param currency currency and amount to transfer
     * @return "from" account balance
     */
    @Transactional
    public double transferMoney(String from, String to, Currency currency) {
        this.getAccountBalances(from, List.of(currency.getCurrency()));

        return 0;
    }
}

