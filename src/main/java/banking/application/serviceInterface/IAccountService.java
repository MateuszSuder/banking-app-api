package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.User;
import banking.application.model.Account;
import banking.application.model.Code;
import banking.application.model.Token;
import banking.application.util.IBAN;
import banking.application.util.AccountType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.List;

/**
 * Interface for service
 */
public interface IAccountService {
    // Stringified JSON to JAVA object mapper.
    ObjectMapper objectMapper = new ObjectMapper();

    Token getAPIToken() throws UnirestException, JsonProcessingException;
    Account getAuthAccount(String userID) throws UnirestException, JsonProcessingException;
    IBAN openAccount(User user, AccountType ac, List<Code> codes);
    List<Code> getUserCodes(String iban);
    void linkAccountToUser(String userID, AccountType ac, IBAN iban) throws UnirestException, JsonProcessingException, ThrowableErrorResponse;
    String getUserAccountIBAN(Account account, AccountType accountType) throws ThrowableErrorResponse;
}
