package banking.application.routes.Account;

import banking.application.global.classes.ThrowableErrorResponse;
import banking.application.global.utils.Auth.User;
import banking.application.routes.Account.BankAccount.Code;
import banking.application.routes.Account.BankAccount.IBAN;
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
