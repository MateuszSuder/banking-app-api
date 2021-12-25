package banking.application.service;

import banking.application.model.Token;
import banking.application.repository.BankAccountRepository;
import banking.application.repository.CodeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.sql.Timestamp;

public abstract class EntryService {
    // Token for accessing Auth0 API
    private Token APIToken = null;

    // Local envs
    protected static final Dotenv dotenv = Dotenv.load();

    // Stringified JSON to JAVA object mapper.
    protected ObjectMapper objectMapper = new ObjectMapper();

    // Template for mongo operations
    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    CodeRepository codeRepository;

    // Method for easy getting Auth0 API user endpoint
    protected String getUserEndpoint(String userID) {
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
    protected Token getAPIToken() throws UnirestException, JsonProcessingException {
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
}
