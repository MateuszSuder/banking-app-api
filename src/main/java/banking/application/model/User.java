package banking.application.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;

/**
 * User POJO class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    String user_id;
    String email;
    UserAccounts userAccounts;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserAccounts getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(UserAccounts userAccounts) {
        this.userAccounts = userAccounts;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", userAccounts=" + userAccounts +
                '}';
    }
}

