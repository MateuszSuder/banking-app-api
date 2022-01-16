package banking.application.model;

import org.springframework.data.annotation.Id;

public class InstallmentsAmountWithId {
    @Id
    int id;
    double amountLeftToPay;

    public int getId() {
        return id;
    }

    public double getAmountLeftToPay() {
        return amountLeftToPay;
    }

    @Override
    public String toString() {
        return "LoanAmountWithId{" +
                "id=" + id +
                ", amountLeftToPay=" + amountLeftToPay +
                '}';
    }
}
