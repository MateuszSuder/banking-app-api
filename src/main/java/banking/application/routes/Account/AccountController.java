package banking.application.routes.Account;

import banking.application.Application;
import banking.application.global.classes.ErrorResponse;
import banking.application.global.interfaces.Auth;
import banking.application.global.utils.Auth.CurrentUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller handling Auth0 account data
 */
@SpringBootApplication
@RequestMapping("/account")
public class AccountController extends Application {
    // Field containing user data
    private CurrentUser currentUser;

    // Controller's service
    @Autowired
    AccountService accountService;

    // Autowired constructor passing current user to class field
    @Autowired
    AccountController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Get endpoint returning user Auth0 account derived from JWT
     * @return User Auth0 account
     */
    @Auth
    @GetMapping("")
    public ResponseEntity GetAuthAccount() {
        try {
            // Get account by Auth0 id
            Account a = this.accountService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());
            return ResponseEntity.status(HttpStatus.OK).body(a);
        } catch (UnirestException | JsonProcessingException e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
