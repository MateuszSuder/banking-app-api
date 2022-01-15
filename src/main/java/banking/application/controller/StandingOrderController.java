package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.model.input.StandingOrderInput;
import banking.application.util.AccountType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller handling standing orders
 */
@SpringBootApplication
@RequestMapping("/standing")
public class StandingOrderController extends Controller {

	@Auth
	@PostMapping("/")
	ResponseEntity<?> AddStandingOrder(@RequestBody StandingOrderInput standingOrderInput) {
		return ResponseEntity.ok(null);
	}

	@Auth
	@DeleteMapping("/{accountType}/{id}")
	ResponseEntity<?> DeleteStandingOrder(@PathVariable AccountType accountType, @PathVariable String id) {
		return ResponseEntity.ok(null);
	}

	@Auth
	@PutMapping("/{accountType}/{id}")
	ResponseEntity<?> ModifyStandingOrder(@PathVariable AccountType accountType, @PathVariable String id, @RequestBody StandingOrderInput standingOrderInput) {
		return ResponseEntity.ok(null);
	}
}
