package banking.application.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
/**
 * POJO class for standing order
 */
public class StandingOrder {
    @Id
    String id;
    String title;
    Recipient to;
    LocalDate nextPayment;
    boolean lastPaymentFailed;
    Currency value;

    public StandingOrder(String title, Recipient to, Currency value) {
        this.id = new ObjectId().toString();
        this.title = title;
        this.to = to;
        this.value = value;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        this.nextPayment = c.getTime().toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        this.lastPaymentFailed = false;
    }

    public StandingOrder(){};

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Recipient getTo() {
        return to;
    }

    public LocalDate getNextPayment() {
        return nextPayment;
    }

    public boolean isLastPaymentFailed() {
        return lastPaymentFailed;
    }

    public Currency getValue() {
        return value;
    }
}
