package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Account;
import banking.application.model.BankAccount;
import banking.application.model.Currency;
import banking.application.model.UserAccounts;
import banking.application.model.input.TransferInput;
import banking.application.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller handling users banking accounts
 */
@SpringBootApplication
@RequestMapping("/account")
public class BankAccountController extends Controller {
    /**
     * Open account for JWT's owner. Disallows creating same type accounts.
     * @param accountType which account to open
     * @return Account's iban or error
     */
    @Auth(codeNeeded = true)
    @PostMapping("open/{accountType}")
    public ResponseEntity<?> OpenAccount(@PathVariable AccountType accountType) {
        // Reusable object
        ResponseEntity<?> alreadyOpened = ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "Conflict",
                        "User has already " + accountType + " account open",
                        409));

        try {
            Account account = this.authService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());
            String iban = this.userService.getUserAccountIBAN(account, accountType);

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

        // Open account, return iban
        IBAN iban = this.accountService.openAccount(this.currentUser.getCurrentUser(), accountType);

        try {
            // Link account to Auth0 user's profile
            this.userService.linkAccountToUser(this.currentUser.getCurrentUser().getUser_id(), accountType, iban);
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

    /**
     * Transfer money from user's chosen account to another account
     * @param accountType type of account to send from
     * @return balance after transfer if success else error
     */
    @Auth(codeNeeded = true)
    @PostMapping("transfer/{accountType}")
    public ResponseEntity TransferMoney(@PathVariable AccountType accountType, @Valid @RequestBody TransferInput transferInput) {

        Currency balance = null;

        try {
            balance = this.accountService.transferMoney(
                    this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType),
                    transferInput.getTo(),
                    transferInput.getValue(),
                    transferInput.getTitle(),
                    TransactionType.MANUAL
            );
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

        return ResponseEntity.ok(balance);
    }

    /**
     * Returns information about user's bank account
     * @param accountType type of account to get info of
     * @return requested bank account information
     */
    @Auth
    @GetMapping("info/{accountType}")
    public ResponseEntity BankAccountInfo(@PathVariable AccountType accountType) {
        UserAccounts userAccounts = this.currentUser.getCurrentUser().getUserAccounts();
        BankAccount bankAccount = null;
        try {
            switch (accountType) {
                case standard:
                    if (userAccounts.getStandard() != null) {
                        bankAccount = this.accountService.bankInfo(userAccounts.getStandard());
                    }
                    break;
                case multi:
                    if (userAccounts.getMulti() != null) {
                       bankAccount = this.accountService.bankInfo(userAccounts.getMulti());
                    }
                    break;
                case crypto:
                    if (userAccounts.getCrypto() != null) {
                        bankAccount = this.accountService.bankInfo(userAccounts.getCrypto());
                    }
                    break;
            }
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

        if(bankAccount != null) {
            return ResponseEntity.status(200).body(bankAccount);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                "Not found",
                "Account with type " + accountType + " not found",
                404));
    }

    /**
     * Returns information about all bank accounts of current ibans
     * @return requested bank account information
     */
    @Auth
    @GetMapping("info")
    public ResponseEntity<?> BankInfoAll() {
        ArrayList<String> allIBANs = this.currentUser.getCurrentUser().getUserAccounts().getAllIBANs();
        return ResponseEntity.status(200).body(this.accountService.bankInfoAll(allIBANs));
    }
}
