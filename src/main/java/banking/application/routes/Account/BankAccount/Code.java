package banking.application.routes.Account.BankAccount;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class generating 8-digits authorization codes
 */
public class Code {
    @Id
    int id;

    String code;

    public Code() {
    }

    // Constructor for filling fields
    public Code(int id, String code) {
        this.id = id;
        this.code = code;
    }

    /**
     * Generate single code
     * @param id id of result code
     * @return single 8-digit code
     */
    public static Code generateSingleCode(int id) {
        Random random = new Random();

        // Get 8-digits int (10000000 - 99999999)
        int code = (10000000 + random.nextInt(90000000));

        // Return new Code object with generated code
        return new Code(id, Integer.toString(code));
    }

    // Standard method for codes generation, generates 8 codes
    public static ArrayList<Code>generateCodes() {
        return Code.generateXCodes(8, 1);
    }

    /**
     * Override standard method, pass quantity and start with index 1
     * @param quantity number of codes
     * @return Array with generated codes
     */
    public static ArrayList<Code> generateXCodes(int quantity) {
        return Code.generateXCodes(quantity, 1);
    }

    /**
     * Generate codes with given quantity
     * @param quantity number of codes
     * @param startingIndex index with which to start
     * @return Generated codes
     */
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
