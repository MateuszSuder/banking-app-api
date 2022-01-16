package banking.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Document for cryptoLevels collection
 */
@Document("cryptoLevels")
public class CryptoLevel {

    @Id
    private int id; // Level

    private int requirements;
    private float fee;
}
