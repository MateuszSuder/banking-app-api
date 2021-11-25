package banking.application.routes.Account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Interface for service
 */
public interface IAccountService {
    // Stringified JSON to JAVA object mapper.
    ObjectMapper objectMapper = new ObjectMapper();

    Token getAPIToken() throws UnirestException, JsonProcessingException;
    Account getAuthAccount(String userID) throws UnirestException, JsonProcessingException;
}
