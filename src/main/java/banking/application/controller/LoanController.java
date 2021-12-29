package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.util.SiteConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller containing loan operations
 */
@SpringBootApplication
@RequestMapping("/loan")
public class LoanController extends Controller {

    @GetMapping("/info")
    public ResponseEntity GetLoanConfig() {
        SiteConfig siteConfig = new SiteConfig();
        return ResponseEntity.ok(siteConfig.getLoanConfig());
    }

    @Auth
    @PostMapping("/take")
    public ResponseEntity<?> TakeLoan() {
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
