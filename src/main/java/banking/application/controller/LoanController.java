package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Loan;
import banking.application.model.input.LoanInput;
import banking.application.model.input.PayLoanInput;
import banking.application.util.AccountType;
import banking.application.util.ErrorResponse;
import banking.application.util.LoanConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Controller containing loan operations
 */
@SpringBootApplication
@RequestMapping("/loan")
public class LoanController extends Controller {

    /**
     * Method to get loan values: min/max length, min/max value, rate
     * @return loan config
     * @throws IllegalAccessException internal
     */
    @GetMapping("/info")
    public ResponseEntity<?> GetLoanConfig() throws IllegalAccessException {
        // Get loanConfig class
        Class<LoanConfig> c = LoanConfig.class;
        // Get class fields
        Field[] fields = c.getDeclaredFields();
        // Create hash map for containing config
        HashMap<String, Object> config = new HashMap<>();
        // Use reflection to get fields and values
        for(Field f : fields) {
            Class<?> type = f.getType();
            if(type == double.class) {
                config.put(f.getName(), f.getDouble(null));
            } else if(type == int.class) {
                config.put(f.getName(), f.getInt(null));
            } else if(type == long.class) {
                config.put(f.getName(), f.getLong(null));
            }
        }
        // Return config
        return ResponseEntity.ok(config);
    }

    /**
     * Endpoint to take loan
     * @param loanInput input of loan
     * @return loan entity or error when no account found (no standard account)
     */
    @Auth(codeNeeded = true)
    @PostMapping("/take")
    public ResponseEntity<?> TakeLoan(@Valid @RequestBody LoanInput loanInput) {
        // If user has open standard account
        if(this.currentUser.getCurrentUser().getUserAccounts().isOpen(AccountType.standard)) {
            try {
                // Use service method to take loan
                Loan loan = this.loanService.takeLoan(this.currentUser.getCurrentUser().getUserAccounts().getStandard(), loanInput);
                return ResponseEntity.ok(loan);
            } catch (ThrowableErrorResponse e) {
                return ResponseEntity.status(e.code).body(e.getErrorResponse());
            }
        }

        return ResponseEntity.status(404).body(
                new ErrorResponse(
                        "Account not found",
                        "User doesn't have standard account open",
                        404)
        );
    }

    /**
     * Endpoint to pay account's loan
     * @param payInput information about payment
     * @return money left after payment or threw error
     */
    @Auth(codeNeeded = true)
    @PutMapping("/pay")
    public ResponseEntity<?> PayLoan(@Valid @RequestBody PayLoanInput payInput) {
        try {
            // Use service method
            double amountLeft = this.loanService.payLoan(
                    this.currentUser.getCurrentUser().getUserAccounts().getStandard(),
                    payInput.getAmount()
            );
            // Map result
            HashMap<String, Double> result = new HashMap<>();
            result.put("amountLeft", amountLeft);
            return ResponseEntity.ok(result);
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }
    }

    /**
     * Endpoint to handle changing value of auto-payment field
     * @param autoPay true for turning on, false for turning off
     * @return null or error
     */
    @Auth(codeNeeded = true)
    @PutMapping("/autoPayment/{autoPay}")
    public ResponseEntity<?> SetAutoPayment(@PathVariable boolean autoPay) {
        try {
            this.loanService.setAutoPayment(this.currentUser.getCurrentUser().getUserAccounts().getStandard(), autoPay);
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }
        return ResponseEntity.ok(null);
    }
}
