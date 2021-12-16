package banking.application.serviceInterface;

import banking.application.model.Account;
import banking.application.model.Code;
import banking.application.model.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Interface for Auth Service
 */
public interface IAuthService {
    Code generateCodeForUser(String userID);
    Account getAuthAccount(String userID) throws UnirestException, JsonProcessingException;
}
