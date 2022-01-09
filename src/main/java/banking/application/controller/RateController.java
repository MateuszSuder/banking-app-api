package banking.application.controller;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.input.ExchangeInput;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

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
}
