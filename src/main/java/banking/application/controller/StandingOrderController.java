package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.input.StandingOrderInput;
import banking.application.util.AccountType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
	ResponseEntity<?> AddStandingOrder(@PathVariable AccountType accountType, @Valid @RequestBody StandingOrderInput standingOrderInput) {
		String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
		try {
			return ResponseEntity.status(201).body(this.standingOrderService.addStandingOrder(iban, standingOrderInput));
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e.getErrorResponse());
		}
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
		String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
		try {
			return ResponseEntity.status(200).body(this.standingOrderService.deleteStandingOrder(iban, id));
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e.getErrorResponse());
		}
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
	ResponseEntity<?> ModifyStandingOrder(@PathVariable AccountType accountType, @PathVariable String id, @Valid @RequestBody StandingOrderInput standingOrderInput) {
		String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
		try {
			return ResponseEntity.ok(this.standingOrderService.modifyStandingOrder(iban, id, standingOrderInput));
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e.getErrorResponse());
		}
	}
}
