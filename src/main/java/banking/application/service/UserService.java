package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.serviceInterface.IUserService;
import banking.application.util.AccountType;
import banking.application.util.IBAN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Service for handling Auth0 account data
 */
@Service
public class UserService extends EntryService implements IUserService {
    // Token for accessing Auth0 API
    private Token APIToken = null;

    // Method for easy getting Auth0 API user endpoint
    public String getUserEndpoint(String userID) {
        // Format path
        return String.format(
                "%1$susers/%2$s",
                dotenv.get("APP_AUTH_API"),
                userID);
    }

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

        // Fetch account
        String response = Unirest
                .get(getUserEndpoint(userID))
                .header(
                        "Authorization", String.format("%1$s %2$s", token.token_type, token.access_token))
                .asString()
                .getBody();

        // Map and return user's account
        return objectMapper.readValue(response, Account.class);
    }

    /**
     * Method saving account data to user's Auth0 profile
     * @param userID Auth0 user id
     * @param ac Account type to be linked
     * @param iban Iban of the account
     * @throws UnirestException when request error occurs
     * @throws JsonProcessingException when json parsing error occurs
     * @throws ThrowableErrorResponse for internal/custom errors
     */
    @Override
    public void linkAccountToUser(String userID, AccountType ac, IBAN iban) throws UnirestException, JsonProcessingException, ThrowableErrorResponse {
        // Get API token
        Token token = getAPIToken();

        // Create JSON objects to send
        JSONObject account = new JSONObject();
        JSONObject metadata = new JSONObject();
        account.put("app_metadata", metadata);

        // Put iban to right field
        switch(ac) {
            case standard:
                metadata.put("standard", iban.getIBAN());
                break;
            case multi:
                metadata.put("multi", iban.getIBAN());
                break;
            case crypto:
                metadata.put("crypto", iban.getIBAN());
                break;
            default:
                throw new ThrowableErrorResponse("Internal error", "Invalid account type", 500);
        }

        // Save data to profile
        Unirest
            .patch(getUserEndpoint(userID))
            .header("Authorization", String.format("%1$s %2$s", token.token_type, token.access_token))
            .header("Content-Type", "application/json")
            .body(account)
            .asString()
            .getBody();
    }

    /**
     * Method to get iban for certain account type
     * @param account user's account
     * @param accountType type of account
     * @return iban of found account or null if not found
     * @throws ThrowableErrorResponse for invalid account type
     */
    @Override
    public String getUserAccountIBAN(Account account, AccountType accountType) throws ThrowableErrorResponse {
        // Check if iban already exists
        if(account.app_metadata != null) {
            switch(accountType) {
                case standard:
                    if(account.app_metadata.standard != null) {
                        return account.app_metadata.standard;
                    }
                    break;
                case multi:
                    if(account.app_metadata.multi != null) {
                        return account.app_metadata.multi;
                    }
                    break;
                case crypto:
                    if(account.app_metadata.crypto != null) {
                        return account.app_metadata.crypto;
                    }
                    break;
                default:
                    throw new ThrowableErrorResponse("Bad Request", "Invalid account type", 400);
            }
        }

        return null;
    }
}


