package banking.application.routes.Account.BankAccount;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Random;

public class Code {
    @Id
    int id;

    String code;

    public Code() {
    }

    public Code(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static Code generateSingleCode(int id) {
        Random random = new Random();

        int code = (10000000 + random.nextInt(90000000));

        return new Code(id, Integer.toString(code));
    }

    public static ArrayList<Code>generateCodes() {
        return Code.generateXCodes(8, 1);
    }

    public static ArrayList<Code> generateXCodes(int quantity) {
        return Code.generateXCodes(quantity, 1);
    }

    public static ArrayList<Code> generateXCodes(int quantity, int startingIndex) {
        ArrayList<Code> codes = new ArrayList<Code>();
        for (int i = startingIndex; i < startingIndex + quantity; i++) {
            codes.add(Code.generateSingleCode(i));
        }

        return codes;
    }

    @Override
    public String toString() {
        return "Code{" +
                "id=" + id +
                ", code='" + code + '\'' +
                '}';
    }
}
