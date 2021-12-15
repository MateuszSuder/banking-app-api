package banking.application.model;

import java.time.LocalTime;

/**
 * POJO class for standing order
 */
public class StandingOrder {
    String title;
    String to; // IBAN
    LocalTime nextPayment;
    boolean lastPaymentFailed;
    Currency currency;
}
