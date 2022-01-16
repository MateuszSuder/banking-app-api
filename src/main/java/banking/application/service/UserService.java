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


