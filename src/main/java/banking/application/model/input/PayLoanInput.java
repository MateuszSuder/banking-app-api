package banking.application.model.input;

import javax.validation.constraints.Positive;

public class PayLoanInput {
	@Positive(message = "Amount field is missing or invalid")
	double amount;

	public PayLoanInput(){}

	public PayLoanInput(double amount) {
		this.amount = amount;
	}

	public double getAmount() {
		return amount;
	}
}
