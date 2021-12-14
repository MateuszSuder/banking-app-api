package banking.application.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/**
 * Document for authorization codes
 */
@Document("codes")
public class Code {

    @Indexed
    int code;

    @Indexed
    @Field("bindTo")
    String userID;

    @Indexed(expireAfterSeconds = 600)
    LocalDateTime timeGenerated;

    public Code(){}

    public Code(String userID, int code, LocalDateTime timeGenerated) {
        this.userID = userID;
        this.code = code;
        this.timeGenerated = timeGenerated;
    }

    public Code(String userID) {
        Random random = new Random();
        this.code = 100000 + random.nextInt(900000);

        this.timeGenerated = LocalDateTime.now(ZoneOffset.UTC);

        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "Kod potwierdzający dla użytkownika o identyfikatorze " + this.userID +
                ": " + this.code + ". Kod utworzony jest ważny 5 minut.";
    }
}
