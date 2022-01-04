package banking.application.model;

import java.util.List;

public class SingleAccountWithToPay {
	double interest;
	double balance;
	int loanId;
	List<InstallmentsAmountWithId> installments;

	public SingleAccountWithToPay(double interest, double balance, int loanId, List<InstallmentsAmountWithId> installments) {
		this.interest = interest;
		this.balance = balance;
		this.loanId = loanId;
		this.installments = installments;
	}

	public double getInterest() {
		return interest;
	}


	public double getBalance() {
		return balance;
	}

	public int getLoanId() {
		return loanId;
	}

	public List<InstallmentsAmountWithId> getInstallments() {
		return installments;
	}
}
