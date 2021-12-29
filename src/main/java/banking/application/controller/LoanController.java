package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.model.input.LoanInput;
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
    public ResponseEntity TakeLoan(@Valid @RequestBody LoanInput loanInput) {
        return ResponseEntity.ok(null);
    }

    @Auth
    @PutMapping("/pay")
    public ResponseEntity<?> PayLoan() {
        return ResponseEntity.ok(null);
    }

    @Auth
    @PutMapping("/autoPayment")
    public ResponseEntity<?> SetAutoPayment() {
        return ResponseEntity.ok(null);
    }
}
