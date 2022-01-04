package banking.application.service;

import banking.application.model.Account;
import banking.application.model.Code;
import banking.application.model.Token;
import banking.application.serviceInterface.IAuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.stereotype.Service;

/**
 * Service for authorization purposes
 * Handles code generation
 */
@Service
public class AuthService extends EntryService implements IAuthService {
    @Override
    public Code generateCodeForUser(String userID) {
        Code code = new Code(userID);
        this.codeRepository.save(code);

        return code;
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
}
