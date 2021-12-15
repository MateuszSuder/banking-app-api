package banking.application.model;

import java.time.LocalTime;
import java.util.List;

/**
 * Class defining Account's loan model
 */
public class Loan {
    LocalTime startedAt;
    LocalTime endsAt;
    float lentAmount;
    float totalToPay;
    List<Installment> installments;
    float interest;
    boolean autoPayment;
}
