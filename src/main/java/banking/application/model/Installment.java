package banking.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.time.LocalTime;
import java.util.Date;

/**
 * POJO class for installments
 */
public class Installment {
    @Id
    Integer id;

    double amount;
    double amountLeftToPay;
    Date paymentDay;

    @Nullable
    Date paidAt;

    public Installment(Integer id, double amount, double amountLeftToPay, Date paymentDay, @Nullable Date paidAt) {
        this.id = id;
        this.amount = amount;
        this.amountLeftToPay = amountLeftToPay;
        this.paymentDay = paymentDay;
        this.paidAt = paidAt;
    }

    public Integer getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public double getAmountLeftToPay() {
        return amountLeftToPay;
    }

    public Date getPaymentDay() {
        return paymentDay;
    }

    @Nullable
    public Date getPaidAt() {
        return paidAt;
    }
}
