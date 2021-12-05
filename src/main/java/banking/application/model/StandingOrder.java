package banking.application.model;

import java.time.LocalTime;

public class StandingOrder {
    String title;
    String to; // IBAN
    LocalTime nextPayment;
    boolean lastPaymentFailed;
    Currency currency;
}
