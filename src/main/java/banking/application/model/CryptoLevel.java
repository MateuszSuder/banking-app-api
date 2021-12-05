package banking.application.model;

import org.springframework.data.annotation.Id;

public class CryptoLevel {

    @Id
    private int id; // Level

    private int requirements;
    private float fee;
}
