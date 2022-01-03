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

    @GetMapping("/info")
    public ResponseEntity<?> GetLoanConfig() throws IllegalAccessException {
        Class<LoanConfig> c = LoanConfig.class;
        Field[] fields = c.getDeclaredFields();
        HashMap<String, Object> config = new HashMap<>();
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
        return ResponseEntity.ok(config);
    }

    @Auth
    @PostMapping("/take")
    public ResponseEntity<?> TakeLoan(@Valid @RequestBody LoanInput loanInput) {
        if(this.currentUser.getCurrentUser().getUserAccounts().isOpen(AccountType.standard)) {
            try {
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

    @Auth
    @PutMapping("/pay")
    public ResponseEntity<?> PayLoan(@Valid @RequestBody PayLoanInput payInput) {
        try {
            System.out.println(payInput.getAmount());
            double amountLeft = this.loanService.payLoan(
                    this.currentUser.getCurrentUser().getUserAccounts().getStandard(),
                    payInput.getAmount()
            );
            HashMap<String, Double> result = new HashMap<>();
            result.put("amountLeft", amountLeft);
            return ResponseEntity.ok(result);
        } catch (ThrowableErrorResponse e) {
            return ResponseEntity.status(e.code).body(e.getErrorResponse());
        }
    }

    @Auth
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
