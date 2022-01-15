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

	/**
	 * Add standing order to user's account which type is given in path variable
	 * Standing order input is given in request's body
	 * @param standingOrderInput standing order information
	 * @param accountType account type from which to delete
	 * @return list of account's standing orders
	 */
	@Auth
	@PostMapping("/{accountType}")
	ResponseEntity<?> AddStandingOrder(@PathVariable AccountType accountType, @RequestBody StandingOrderInput standingOrderInput) {
		return ResponseEntity.ok(null);
	}

	/**
	 * Delete standing order of user's account which type is given in path variable
	 * @param accountType account type of which to delete from
	 * @param id id of standing order to delete
	 * @return list of account's standing orders
	 */
	@Auth
	@DeleteMapping("/{accountType}/{id}")
	ResponseEntity<?> DeleteStandingOrder(@PathVariable AccountType accountType, @PathVariable String id) {
		return ResponseEntity.ok(null);
	}

	/**
	 * Modify existing standing order
	 * @param accountType type account from which to modify
	 * @param id id of standing order to modify
	 * @param standingOrderInput modify input
	 * @return list of account's standing orders
	 */
	@Auth
	@PutMapping("/{accountType}/{id}")
	ResponseEntity<?> ModifyStandingOrder(@PathVariable AccountType accountType, @PathVariable String id, @RequestBody StandingOrderInput standingOrderInput) {
		return ResponseEntity.ok(null);
	}
}
