package banking.application.routes.Account;

import banking.application.Application;
import banking.application.global.classes.ErrorResponse;
import banking.application.global.interfaces.Auth;
import banking.application.global.utils.Auth.CurrentUser;
import banking.application.routes.Account.BankAccount.IBAN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Path variable or multiple endpoints?
    @Auth
    @PostMapping("open/{accountType}")
    public ResponseEntity OpenAccount(@PathVariable AccountType accountType) {
        if(this.currentUser.getCurrentUser().getUserAccounts().isOpen(accountType)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(
                            "Conflict",
                            "User has already " + accountType + " account open",
                            409));
        }
        IBAN iban = this.accountService.openAccount(this.currentUser.getCurrentUser().getUser_id(), accountType);
        return ResponseEntity.status(HttpStatus.CREATED).body(iban.getIBAN());
    }

    @Auth
    @GetMapping("")
    public ResponseEntity GetAuthAccount() {
        try {
            // Get account by Auth0 id
            String id = this.currentUser.getCurrentUser().getUser_id();
            Account a = this.accountService.getAuthAccount(id);

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
