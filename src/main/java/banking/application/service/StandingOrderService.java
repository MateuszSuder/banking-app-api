package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.*;
import banking.application.model.input.StandingOrderInput;
import banking.application.serviceInterface.IStandingOrderService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Service handling standing order actions
 */
@Service
public class StandingOrderService extends EntryService implements IStandingOrderService {

	/**
	 * Get all standing order bound for given iban
	 * @param iban iban of account to search
	 * @return list of standing orders
	 */
	@Override
	public List<StandingOrder> getListOfStandingOrders(String iban) {
		return this.bankAccountRepository.getListOfStandingOrders(iban);
	}

	/**
	 * Add new standing order and bind it to account
	 * @param iban iban of account to bind to
	 * @param input standing order input
	 * @return list of account's standing orders
	 * @throws ThrowableErrorResponse when account not found
	 */
	@Override
	public List<StandingOrder> addStandingOrder(String iban, StandingOrderInput input) throws ThrowableErrorResponse {
		StandingOrder order = new StandingOrder(input.getTitle(), input.getTo(), input.getValue());

		Query query = new Query(Criteria.where("_id").is(iban));
		Update update = new Update().push("standingOrders", order);

		UpdateResult result = this.mongoTemplate.updateFirst(query, update, BankAccount.class);

		if (result.getMatchedCount() == 0)
			throw new ThrowableErrorResponse(
					"Not found",
					"Account with iban " + iban + " not found",
					404);

		return this.getListOfStandingOrders(iban);
	}

	/**
	 * Delete standing order with given id
	 * @param iban iban of account
	 * @param id id of standing order to delete
	 * @return list of account's standing orders
	 * @throws ThrowableErrorResponse when account not found or id not found in list of standing orders
	 */
	@Override
	public List<StandingOrder> deleteStandingOrder(String iban, String id) throws ThrowableErrorResponse {
		Query query = new Query(Criteria.where("_id").is(iban));
		Update update = new Update().pull("standingOrders", Query.query(Criteria.where("_id").is(id)));

		UpdateResult result = this.mongoTemplate.updateFirst(query, update, BankAccount.class);

		if (result.getModifiedCount() == 0)
			throw new ThrowableErrorResponse(
					"Not found",
					"Account with iban " + iban + " not found or order with id " + id + " is not bound to this account",
					404);

		return this.getListOfStandingOrders(iban);
	}

	/**
	 * Replace standing order with given id with input given as argument
	 * @param iban iban of account
	 * @param id id of standing order to modify
	 * @param input standing order input to replace with
	 * @return list of account's standing orders
	 * @throws ThrowableErrorResponse when account not found or id not found in list of standing orders
	 */
	@Override
	public List<StandingOrder> modifyStandingOrder(String iban, String id, StandingOrderInput input) throws ThrowableErrorResponse {
		StandingOrder order = new StandingOrder(input.getTitle(), input.getTo(), input.getValue());

		Query query = new Query(Criteria.where("_id").is(iban)).addCriteria(Criteria.where("standingOrders._id").is(id));
		Update update = new Update().set("standingOrders.$", order);

		UpdateResult result = this.mongoTemplate.updateFirst(query, update, BankAccount.class);

		if (result.getModifiedCount() == 0)
			throw new ThrowableErrorResponse(
					"Not found",
					"Account with iban " + iban + " not found or order with id " + id + " is not bound to this account",
					404);

		return this.getListOfStandingOrders(iban);
	}

	/**
	 * Get list of accounts with standing order to fill
	 * @return account id with standing order
	 */
	public List<AccountWithStandingOrderInfo> getStandingOrdersToPayToday() {
		return this.bankAccountRepository.getAccountsWithToPayToday();
	}

	/**
	 * Change next payment date and success info
	 * @param a account with standing order info
	 * @param filled if success
	 */
	public void changeStandingOrderValues(AccountWithStandingOrderInfo a, boolean filled) {
		Query query = new Query(Criteria.where("_id").is(a.getId())).addCriteria(Criteria.where("standingOrders._id").is(a.getStandingOrder().getId()));
		Calendar c = Calendar.getInstance();
		if(filled) {
			c.add(Calendar.MONTH, 1);
			Update update = new Update().set("standingOrders.$.nextPayment", c.getTime()).set("standingOrders.$.lastPaymentFailed", false);

			this.mongoTemplate.updateFirst(query, update, BankAccount.class);
		} else {
			c.add(Calendar.DAY_OF_MONTH, 1);
			Update update = new Update()
					.set("standingOrders.$.nextPayment", c.getTime())
					.set("standingOrders.$.lastPaymentFailed", true)
					.push("alertsList", new Alert("Insufficient funds",
							"Your balance is too low to pay standing order. Payment will be retried tomorrow"
					));

			this.mongoTemplate.updateFirst(query, update, BankAccount.class);
		}
	}
}
