package banking.application.routes.Account;

import banking.application.routes.Account.BankAccount.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Service for handling Auth0 account data
 */
@Service
public class AccountService implements IAccountService {
    // Local envs
    private static final Dotenv dotenv = Dotenv.load();
    // Token for accessing Auth0 API
    private Token APIToken = null;
    @Autowired
    BankAccountRepository bankAccountRepository;

    /**
     * Method used to fetch token or return valid, existing one
     * @return Auth0 api token
     * @throws UnirestException Request error
     * @throws JsonProcessingException JSON parsing error
     */
    @Override
    public Token getAPIToken() throws UnirestException, JsonProcessingException {
        // Get system time
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Check if cached token is still valid
        if(this.APIToken != null && APIToken.getExpiresAt() > timestamp.getTime()) {
            return APIToken;
        }

        // Fetch token
        String response = Unirest.post(String.format("%soauth/token", dotenv.get("APP_DOMAIN")))
                .header("content-type", "application/x-www-form-urlencoded")
                .body(String.format(
                        "grant_type=client_credentials&client_id=%1$s&client_secret=%2$s&audience=%3$sapi/v2/",
                        dotenv.get("API_CLIENT_ID"),
                        dotenv.get("API_CLIENT_SECRET"),
                        dotenv.get("APP_DOMAIN")))
                .asString()
                .getBody();

        // Map token to class then return it to field
        this.APIToken = objectMapper.readValue(response, Token.class);
        return this.APIToken;
    }

    /**
     * Method fetching user's profile
     * @param userID user Auth0 id
     * @return User's Auth0 account
     * @throws UnirestException Request error
     * @throws JsonProcessingException JSON parsing error
     */
    @Override
    public Account getAuthAccount(String userID) throws UnirestException, JsonProcessingException {
        // Get API token
        Token token = getAPIToken();

        // Format path
        String user = String.format(
                "%1$susers/%2$s",
                dotenv.get("APP_AUTH_API"),
                userID);

        // Fetch account
        String response = Unirest
                .get(user)
                .header(
                        "Authorization", String.format("%1$s %2$s", token.token_type, token.access_token))
                .asString()
                .getBody();

        // Map and return user's account
        return objectMapper.readValue(response, Account.class);
    }

    @Override
    public IBAN openAccount(String userID, AccountType ac) {
        IBAN iban = new IBAN(ac, userID);
        ArrayList<Code> codes = Code.generateCodes();

        BankAccount account = new BankAccount(iban.getIBAN(), Arrays.asList(new Currency("PLN", 10000F)), codes);

        this.bankAccountRepository.save(account);

        return iban;
    }

    @Override
    public void linkAccountToUser(String userID, AccountType ac, IBAN iban) {

    }
}
