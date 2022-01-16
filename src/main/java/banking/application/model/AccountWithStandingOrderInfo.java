package banking.application.model;

import org.springframework.data.annotation.Id;

public class AccountWithStandingOrderInfo {
	@Id
	String id;

	StandingOrder standingOrder;
	boolean toPayToday;

	AccountWithStandingOrderInfo(){}

	public String getId() {
		return id;
	}

	public StandingOrder getStandingOrder() {
		return standingOrder;
	}

	public boolean isToPayToday() {
		return toPayToday;
	}
}
