package banking.application.routes.Account;

import banking.application.routes.Account.BankAccount.BankAccountRepository;
import banking.application.routes.Account.BankAccount.IBAN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Interface for service
 */
public interface IAccountService {
    // Stringified JSON to JAVA object mapper.
    ObjectMapper objectMapper = new ObjectMapper();

    Token getAPIToken() throws UnirestException, JsonProcessingException;
    Account getAuthAccount(String userID) throws UnirestException, JsonProcessingException;
    IBAN openAccount(String userID, AccountType ac);
    void linkAccountToUser(String userID, AccountType ac, IBAN iban);
}
