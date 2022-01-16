package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.AccountWithStandingOrderInfo;
import banking.application.model.StandingOrder;
import banking.application.model.input.StandingOrderInput;

import java.util.List;

public interface IStandingOrderService {
	List<StandingOrder> getListOfStandingOrders(String iban);
	List<StandingOrder> addStandingOrder(String iban, StandingOrderInput input) throws ThrowableErrorResponse;
	List<StandingOrder> deleteStandingOrder(String iban, String id) throws ThrowableErrorResponse;
	List<StandingOrder> modifyStandingOrder(String iban, String id, StandingOrderInput input) throws ThrowableErrorResponse;
	List<AccountWithStandingOrderInfo> getStandingOrdersToPayToday();
	void changeStandingOrderValues(AccountWithStandingOrderInfo a, boolean filled);
}
