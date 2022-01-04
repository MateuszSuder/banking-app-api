package banking.application.model;

import org.springframework.data.annotation.Id;

import java.util.List;

public class AccountAbleToPay {
    @Id
    String id;

    boolean ableToPay;
    List<InstallmentsAmountWithId> installments;
    double interest;
    double toPay;
    int loanId;

    public String getId() {
        return id;
    }

    public List<InstallmentsAmountWithId> getInstallments() {
        return installments;
    }

    public int getLoanId() {
        return loanId;
    }

    public double getToPay() {
        return toPay;
    }

    public double getInterest() {
        return interest;
    }

    public boolean isAbleToPay() {
        return ableToPay;
    }

    @Override
    public String toString() {
        return "AccountAbleToPay{" +
                "id='" + id + '\'' +
                ", installments=" + installments +
                ", loanId=" + loanId +
                ", toPay=" + toPay +
                ", interest=" + interest +
                ", ableToPay=" + ableToPay +
                '}';
    }
}
