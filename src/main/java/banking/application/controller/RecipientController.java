package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Recipient;
import banking.application.util.AccountType;
import banking.application.util.ErrorResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller handling saved recipients
 */
@SpringBootApplication
@RequestMapping("/recipient")
public class RecipientController extends Controller {

    /**
     * Endpoint handling adding recipient to account
     * @param accountType type of account
     * @param recipient recipient input
     * @return current list of recipients of given account
     */
    @Auth
    @PostMapping("/{accountType}")
    ResponseEntity<?> AddRecipient(@PathVariable AccountType accountType, @Valid @RequestBody Recipient recipient) {
        String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
        if(iban == null) return ResponseEntity.status(404).body(new ErrorResponse(
                "Not found",
                "Didn't find account iban for " + accountType,
                404));
        try {
            return ResponseEntity.status(201).body(this.recipientService.addRecipient(iban, recipient));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

    }

    /**
     * Endpoint handling deleting recipient from account
     * @param accountType type of account
     * @param recipientIban recipient iban
     * @return current list of recipients of given account
     */
    @Auth
    @DeleteMapping("/{accountType}")
    ResponseEntity<?> DeleteRecipient(@PathVariable AccountType accountType, @RequestParam String recipientIban) {
        String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
        try {
            return ResponseEntity.status(200).body(this.recipientService.deleteRecipient(iban, recipientIban));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

    }

    /**
     * Endpoint handling modifying recipient in account
     * @param accountType type of account
     * @param recipient recipient input
     * @return current list of recipients of given account
     */
    @Auth
    @PutMapping("/{accountType}")
    ResponseEntity<?> ModifyRecipient(@PathVariable AccountType accountType, @Valid @RequestBody Recipient recipient) {
        String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
        try {
            return ResponseEntity.status(200).body(this.recipientService.modifyRecipient(iban, recipient));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }

    }
}
