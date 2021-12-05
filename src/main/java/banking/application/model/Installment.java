package banking.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.time.LocalTime;

public class Installment {
    @Id
    Integer id;

    float amount;
    float amountLeftToPay;
    LocalTime paymentDay;

    @Nullable
    LocalTime paidAt;
}
