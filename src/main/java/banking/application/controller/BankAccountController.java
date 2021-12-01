package banking.application.controller;

import banking.application.Application;
import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Account;
import banking.application.model.Code;
import banking.application.service.AccountService;
import banking.application.service.MailerService;
import banking.application.util.AccountType;
import banking.application.util.CurrentUser;
import banking.application.util.ErrorResponse;
import banking.application.util.IBAN;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller handling users banking accountsv
 */
@SpringBootApplication
@RequestMapping("/account")
public class BankAccountController extends Application {
    // Field containing user data
    private CurrentUser currentUser;
    // Controller's service
    @Autowired
    AccountService accountService;

    @Autowired
    MailerService mailerService;

    // Autowired constructor passing current user to class field
    @Autowired
    BankAccountController(CurrentUser currentUser) {
        this.currentUser = currentUser;
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
        } catch (JsonProcessingException | UnirestException e) {
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
