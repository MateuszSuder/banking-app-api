package banking.application.controller;

import banking.application.Application;
import banking.application.model.Code;
import banking.application.util.ErrorResponse;
import banking.application.annotation.Auth;
import banking.application.util.CurrentUser;
import banking.application.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling user account data
 */
@SpringBootApplication
@RequestMapping("/user")
public class UserController extends Application {
    // Field containing user data
    private CurrentUser currentUser;

    // Autowired constructor passing current user to class field
    @Autowired
    UserController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }


    /**
     * Get user Auth0 account
     * @return user's Auth0 profile
     */
    @Auth
    @GetMapping("")
    public ResponseEntity GetAuthAccount() {
        try {
            // Get account by Auth0 id
            String id = this.currentUser.getCurrentUser().getUser_id();
            Account a = this.userService.getAuthAccount(id);

            return ResponseEntity.status(HttpStatus.OK).body(a);
        } catch (UnirestException | JsonProcessingException e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Method generating one-use authorization code.
     * Code is then sent to user's email.
     * @return
     */
    @Auth
    @PostMapping("code")
    public ResponseEntity generateCode() {
        Code code = this.authService.generateCodeForUser(this.currentUser.getCurrentUser().getUser_id());

        String email = this.currentUser.getCurrentUser().getEmail();
        String message = code.toString();

        this.mailerService.sendMessage(email, "Kod potwierdzający", message);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
