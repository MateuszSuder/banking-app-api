package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Account;
import banking.application.model.input.TransactionPageInput;
import banking.application.model.output.TransactionPageOutput;
import banking.application.util.AccountType;
import banking.application.util.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller handling request for transactions data
 */
@SpringBootApplication
@RequestMapping("/transaction")
public class TransactionController extends Controller {
    /**
     * Returns transaction with specific filters and pagination
     * @param accountType type of user's account to get list of transactions
     * @param transactionPageInput transaction filters
     * @return Transaction page and pagination
     */
    @Auth
    @PostMapping("/{accountType}")
    ResponseEntity getTransactionsPage(@PathVariable AccountType accountType, @Valid @RequestBody TransactionPageInput transactionPageInput) {
        try {
            // Get user's account iban
            Account account = this.authService.getAuthAccount(this.currentUser.getCurrentUser().getUser_id());
            String iban = this.userService.getUserAccountIBAN(account, accountType);
            // Get transaction page
            TransactionPageOutput ts = this.transactionService.getTransactionsPage(iban, transactionPageInput);
            return ResponseEntity.ok(ts);
        } catch (JsonProcessingException | UnirestException e) {
            e.printStackTrace();
            // Return error if error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }
    }
}
