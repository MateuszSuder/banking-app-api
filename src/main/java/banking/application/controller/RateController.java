package banking.application.controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public ResponseEntity<?> GetRates() {
		return null;
	}
}
