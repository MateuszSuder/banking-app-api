package banking.application.routes.Account;

import banking.application.Application;
import banking.application.global.classes.ThrowableErrorResponse;
import banking.application.global.classes.ErrorResponse;
import banking.application.global.interfaces.Auth;
import banking.application.global.utils.Auth.CurrentUser;
import banking.application.global.utils.Mailer.MailerService;
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

    /**
     * Open account for JWT's owner. Disallows creating same type accounts.
     * @param accountType which account to open
     * @return Account's iban or error
     */
    @Auth
    @PostMapping("open/{accountType}")
    public ResponseEntity OpenAccount(@PathVariable AccountType accountType) {
        // Reusable object
        ResponseEntity alreadyOpened = ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "Conflict",
                        "User has already " + accountType + " account open",
                        409));
        // Check if account already exists in user's JWT
        if(this.currentUser.getCurrentUser().getUserAccounts().isOpen(accountType)) {
            return alreadyOpened;
        }

        try {
            // Double check if user already has account in Auth0 profile
            Account account = this.accountService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());

            // Check if iban already exists
            if(account.app_metadata != null) {
                switch(accountType) {
                    case standard:
                        if(account.app_metadata.standard != null) {
                            return alreadyOpened;
                        }
                        break;
                    case multi:
                        if(account.app_metadata.multi != null) {
                            return alreadyOpened;
                        }
                        break;
                    case crypto:
                        if(account.app_metadata.crypto != null) {
                            return alreadyOpened;
                        }
                        break;
                    default:
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Bad Request", "Invalid account type", 400));
                }
            }
        } catch (JsonProcessingException | UnirestException  e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        /**
         * Why do we check 2 times?
         * In case of not refreshing JWT (not going through auth after creating account), we need to
         * check profile, because JWT may have old data
         */

        // Open account, return iban
        IBAN iban = this.accountService.openAccount(this.currentUser.getCurrentUser(), accountType);

        try {
            // Link account to Auth0 user's profile
            this.accountService.linkAccountToUser(this.currentUser.getCurrentUser().getUser_id(), accountType, iban);
        } catch (UnirestException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

        // Return iban if created
        return ResponseEntity.status(HttpStatus.CREATED).body(iban.getIBAN());
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
