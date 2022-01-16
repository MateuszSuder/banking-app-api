package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.AccountWithStandingOrderInfo;
import banking.application.model.SiteConfig;
import banking.application.model.input.StandingOrderInput;
import banking.application.util.AccountType;
import banking.application.util.TransactionType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

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
	@Auth(codeNeeded = true)
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
	@Auth(codeNeeded = true)
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
	@Auth(codeNeeded = true)
	@PutMapping("/{accountType}/{id}")
	ResponseEntity<?> ModifyStandingOrder(@PathVariable AccountType accountType, @PathVariable String id, @Valid @RequestBody StandingOrderInput standingOrderInput) {
		String iban = this.currentUser.getCurrentUser().getUserAccounts().getIban(accountType);
		try {
			return ResponseEntity.ok(this.standingOrderService.modifyStandingOrder(iban, id, standingOrderInput));
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e.getErrorResponse());
		}
	}


	/**
	 * Checks for unfilled standing orders and executes them
	 */
	@Scheduled(cron = "0 0 10 ? * *")
	@Transactional
	@PostConstruct
	public void fillStandingOrders() {
		List<AccountWithStandingOrderInfo> list = this.standingOrderService.getStandingOrdersToPayToday();
		for(AccountWithStandingOrderInfo a : list) {
			try {
				this.accountService.transferMoney(
						a.getId(),
						a.getStandingOrder().getTo(),
						a.getStandingOrder().getValue(),
						a.getStandingOrder().getTitle(),
						TransactionType.STANDING_ORDER
				);
				this.standingOrderService.changeStandingOrderValues(a, true);
			} catch (ThrowableErrorResponse e) {
				this.standingOrderService.changeStandingOrderValues(a, false);
			}
		}
	}
}
