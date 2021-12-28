package banking.application.controller;

import banking.application.util.SiteConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
