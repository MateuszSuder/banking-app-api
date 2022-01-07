package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.serviceInterface.IAccountService;
import banking.application.util.AccountType;
import banking.application.util.Currencies;
import banking.application.util.IBAN;
import banking.application.util.TransactionType;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        BankAccount account = null;

        // Creating BankAccount instance and adding to it balance for testing purposes
        switch(ac) {
            case standard:
                account = new BankAccount(iban.getIBAN(), List.of(new Currency(Currencies.PLN, 10000)));
                break;
            case multi:
                account = new BankAccount(iban.getIBAN(), List.of(new Currency(Currencies.USD, 2500)));
                break;
            case crypto:
                account = new BankAccount(iban.getIBAN(), List.of(new Currency(Currencies.USDT, 2500)));
        }

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
     * todo try to optimalize this
     * Method transferring money from one account to another
     * @param from from which account
     * @param to to which account
     * @param currency currency and amount to transfer
     * @return "from" account balance
     */
    @Transactional
    public Currency transferMoney(String from, Recipient to, Currency currency, String title, TransactionType transactionType) throws ThrowableErrorResponse {
        try {
            String accountNumber = to.getAccountNumber();

            if(from.equals(accountNumber))
                throw new ThrowableErrorResponse("From equals to", "Account's IBANs are the same", 409);

            // Transfers available only from same accounts
            if(IBAN.getAccountType(from) != IBAN.getAccountType(accountNumber))
                throw new ThrowableErrorResponse("Different account types", "Accounts are of other account types", 400);

            // Get currency amount from sender
            Currency c = this.getAccountBalances(from, List.of(currency.getCurrency())).get(0);

            // Throw error if not enough currency
            if(c.getAmount() < currency.getAmount())
                throw new ThrowableErrorResponse("Insufficient funds", "Account with iban " + from + " doesn't have enough funds", 406);

            // Find recipient balance
            List<Currency> toCurrencies = this.getAccountBalances(accountNumber, List.of(currency.getCurrency()));

            // If account not found
            if(toCurrencies.size() == 0) {
                throw new ThrowableErrorResponse("Account not or currency not found",
                        "Account with iban " + to + "doesn't exists or doesn't have this type of currency",
                        404);
            }

            // Find account's currency and subtract transferred value
            Query fromQuery = new Query().addCriteria(Criteria.where("_id").is(from)).addCriteria(Criteria.where("currencies.currency").is(currency.getCurrency()));
            Update fromUpdate = new Update().inc("currencies.$.amount", -currency.getAmount());
            mongoTemplate.updateFirst(fromQuery, fromUpdate, BankAccount.class);

            // Find account's currency and add transferred value
            Query toQuery = new Query().addCriteria(Criteria.where("_id").is(accountNumber)).addCriteria(Criteria.where("currencies.currency").is(currency.getCurrency()));
            Update toUpdate = new Update().inc("currencies.$.amount", currency.getAmount());
            mongoTemplate.updateFirst(toQuery, toUpdate, BankAccount.class);

            Transaction transaction = new Transaction(from, to, title, currency, transactionType);
            this.transactionRepository.insert(transaction);

            // Return account balance
            return new Currency(currency.getCurrency(), c.getAmount() - currency.getAmount());
        } catch (IllegalArgumentException e) {
            // Throw for illegal iban
            throw new ThrowableErrorResponse("Invalid iban", e.getMessage(), 400);
        }
    }

    /**
     * Returns <code>BankAccount</code> for given <code>IBAN</code>
     * @param IBAN requested bank account's IBAN
     * @return bank account info for given IBAN
     * @throws ThrowableErrorResponse for invalid IBAN
     */
    @Override
    public BankAccount bankInfo(String IBAN) throws ThrowableErrorResponse {
        Optional<BankAccount> info = this.bankAccountRepository.findById(IBAN);
        if(info.isPresent()) return info.get();
        throw new ThrowableErrorResponse("Invalid iban", "Account with IBAN " + IBAN + " not found", 500);
    }

    @Override
    public Iterable<BankAccount> bankInfoAll(ArrayList<String> iban) {
        return this.bankAccountRepository.findAllById((iban));
    }


}

