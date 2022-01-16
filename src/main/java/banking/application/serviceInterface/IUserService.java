package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.User;
import banking.application.model.Account;
import banking.application.model.Token;
import banking.application.util.IBAN;
import banking.application.util.AccountType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Interface for service
 */
public interface IUserService {
    void linkAccountToUser(String userID, AccountType ac, IBAN iban) throws UnirestException, JsonProcessingException, ThrowableErrorResponse;
    String getUserAccountIBAN(Account account, AccountType accountType) throws ThrowableErrorResponse;
}
