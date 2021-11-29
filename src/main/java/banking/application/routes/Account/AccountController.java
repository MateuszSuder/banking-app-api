package banking.application.routes.Account;

import banking.application.Application;
import banking.application.global.classes.ThrowableErrorResponse;
import banking.application.global.classes.ErrorResponse;
import banking.application.global.interfaces.Auth;
import banking.application.global.utils.Auth.CurrentUser;
import banking.application.global.utils.Mailer.MailerService;
import banking.application.routes.Account.BankAccount.Code;
import banking.application.routes.Account.BankAccount.IBAN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Autowired
    MailerService mailerService;

    // Autowired constructor passing current user to class field
    @Autowired
    AccountController(CurrentUser currentUser) {
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

    /**
     * Send codes to user's email derived from Auth0 account
     * @param accountType type of account to sends codes
     * @return null
     */
    @Auth
    @GetMapping("codes/{accountType}")
    public ResponseEntity SendCodes(@PathVariable AccountType accountType) {
        String iban = null;

        try {
            Account account = this.accountService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());
            iban = this.accountService.getUserAccountIBAN(account, accountType);
            if(iban == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        new ErrorResponse(
                            "Account doesn't exists",
                            "Account of type " + accountType + " is not open for user " + this.currentUser.getCurrentUser().getUser_id(),
                            404));

            List<Code> codes = this.accountService.getUserCodes(iban);
            this.mailerService.sendCodes(this.currentUser.getCurrentUser().getEmail(), codes, iban);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (JsonProcessingException | UnirestException  e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (ThrowableErrorResponse throwableErrorResponse) {
            return ResponseEntity.status(throwableErrorResponse.code).body(throwableErrorResponse.getErrorResponse());
        }
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

        try {
            Account account = this.accountService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());
            String iban = this.accountService.getUserAccountIBAN(account, accountType);

            if (iban != null) {
                return alreadyOpened;
            }
        } catch (JsonProcessingException | UnirestException  e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (ThrowableErrorResponse throwableErrorResponse) {
            return ResponseEntity.status(throwableErrorResponse.code).body(throwableErrorResponse.getErrorResponse());
        }

        // Generate codes
        ArrayList<Code> codes = Code.generateCodes();

        // Open account, return iban
        IBAN iban = this.accountService.openAccount(this.currentUser.getCurrentUser(), accountType, codes);

        this.mailerService.sendCodes(this.currentUser.getCurrentUser().getEmail(), codes, iban);

        try {
            // Link account to Auth0 user's profile
            this.accountService.linkAccountToUser(this.currentUser.getCurrentUser().getUser_id(), accountType, iban);
        } catch (UnirestException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

        // Prepare json-like response
        HashMap<String, String> json = new HashMap<String, String>();
        json.put("IBAN_raw", iban.getIBAN());
        json.put("IBAN", iban.toString());

        // Return iban if created
        return ResponseEntity.status(HttpStatus.CREATED).body(json);
    }
}
