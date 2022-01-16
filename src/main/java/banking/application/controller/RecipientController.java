package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Rate;
import banking.application.model.Recipient;
import banking.application.model.input.TransferInput;
import banking.application.util.AccountType;
import banking.application.util.ErrorResponse;
import banking.application.util.TransactionType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    @Auth(codeNeeded = true)
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
    @Auth(codeNeeded = true)
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
    @Auth(codeNeeded = true)
    @PutMapping("/{accountType}")
    ResponseEntity<?> ModifyRecipient(@PathVariable AccountType accountType, @Valid @RequestBody Recipient recipient) {
        String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
        try {
            return ResponseEntity.status(200).body(this.recipientService.modifyRecipient(iban, recipient));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }
    }

    /**
     * Endpoint handling sending money to saved recipient
     * @param accountType type of account
     * @param transferInput transferInput
     * @return balance after money transfer
     */
    @Auth
    @PostMapping("/{accountType}/send")
    ResponseEntity<?> SendToRecipient(@PathVariable AccountType accountType, @Valid @RequestBody TransferInput transferInput) {
        String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
        List<Recipient> recipients = this.recipientService.getAllRecipients(iban);
        Recipient recipient = recipients.stream()
                .filter(r -> r.getAccountNumber().equals(transferInput.getTo().getAccountNumber()))
                .findAny()
                .orElse(null);
        if(recipient == null) {
            return ResponseEntity.status(404).body(new ErrorResponse(
                    "Not found",
                    "Recipient not bound to an account",
                    404));
        }

        try {
            return ResponseEntity.status(200).body(this.accountService.transferMoney(
                    iban,
                    transferInput.getTo(),
                    transferInput.getValue(),
                    transferInput.getTitle(),
                    TransactionType.STANDING_ORDER));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }


    }
}
