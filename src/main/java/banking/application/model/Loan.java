package banking.application.model;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Class defining Account's loan model
 */
public class Loan {
    Date startedAt;
    Date endsAt;
    double lentAmount;
    double totalToPay;
    List<Installment> installments;
    double interest;
    boolean autoPayment;

    public Loan(Date startedAt, Date endsAt, double lentAmount, double totalToPay, List<Installment> installments, double interest, boolean autoPayment) {
        this.startedAt = startedAt;
        this.endsAt = endsAt;
        this.lentAmount = lentAmount;
        this.totalToPay = totalToPay;
        this.installments = installments;
        this.interest = interest;
        this.autoPayment = autoPayment;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getEndsAt() {
        return endsAt;
    }

    public double getLentAmount() {
        return lentAmount;
    }

    public double getTotalToPay() {
        return totalToPay;
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public double getInterest() {
        return interest;
    }

    public boolean isAutoPayment() {
        return autoPayment;
    }
}
