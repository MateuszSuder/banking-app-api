package banking.application.controller;

import banking.application.annotation.Auth;
import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Currency;
import banking.application.model.input.ExchangeInput;
import banking.application.util.ErrorResponse;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller containing exchange rates operations
 */
@SpringBootApplication
@RequestMapping("/rate")
public class RateController extends Controller {

	/**
	 * Method returning all exchange rates
	 * @return list of exchange rates
	 */
	@GetMapping("/")
	public ResponseEntity<?> GetRates() {
		return ResponseEntity.status(200).body(this.rateService.getRates());
	}

	/**
	 * Get info about exchange
	 * @return exchange result
	 */
	@GetMapping("/info")
	public ResponseEntity<?> ExchangeInfo(@Valid @RequestBody ExchangeInput exchangeInput) {
		try {
			return ResponseEntity.status(200).body(this.rateService.exchangeInfo(exchangeInput.getFrom(), exchangeInput.getTo()));
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e.getErrorResponse());
		}
	}

	/**
	 * Exchange currency
	 * @param exchangeInput exchange input
	 * @return Exchange info
	 */
	@Auth(codeNeeded = true)
	@PostMapping("")
	public ResponseEntity<?> Exchange(@Valid @RequestBody ExchangeInput exchangeInput) {
		String iban = this.currentUser.getCurrentUser().getUserAccounts().getMulti();

		if(iban == null) {
			return ResponseEntity.status(400).body(
					new ErrorResponse(
							"Bad request",
							"Multi account not open",
							400
					)
			);
		}

		List<Currency> currencies = this.accountService.getAccountBalances(iban,
				List.of(
						exchangeInput.getFrom().getCurrency(),
						exchangeInput.getTo()));

		if(currencies.size() == 0 || currencies.get(0).getAmount() < exchangeInput.getFrom().getAmount()) {
			return ResponseEntity.status(400).body(
					new ErrorResponse(
							"Bad request",
							"Not enough funds",
							400
					)
			);
		}

		try {
			return ResponseEntity.status(200).body(
					this.rateService.exchange(
							iban, currencies.size() == 1, this.rateService.exchangeInfo(exchangeInput.getFrom(), exchangeInput.getTo())
					)
			);
		} catch (ThrowableErrorResponse e) {
			return ResponseEntity.status(e.code).body(e);
		}
	}
}
