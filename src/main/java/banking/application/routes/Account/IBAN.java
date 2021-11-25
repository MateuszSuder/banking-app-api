package banking.application.routes.Account;


import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class IBAN {
    private int[] checksum = new int[2];
    private int[] bankCode = new int[8];
    private int[] accountNumber = new int[16];

    IBAN(AccountType at, String userID) {
        String bankCodeBase = "MS0DP0MW";
        Integer sum = 0;
        char[] charArray = bankCodeBase.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            bankCode[i] = Character.getNumericValue(charArray[i]) % 10;
        }

        accountNumber[0] = 0;
        accountNumber[1] = at.getValue();

        char[] idAsChars = userID.substring(userID.length() - 6).toCharArray();

        for (int i = 0; i < idAsChars.length; i++) {
            accountNumber[i + 2] = idAsChars[i] % 10;
        }

        // Get random 8 digits for account number
        for (int i = 8; i < 16; i++) {
            Random rand = new Random();

            // Obtain a number between [0 - 9].
            int n = rand.nextInt(10);

            accountNumber[i] = n;
        }

        // Calculation checksum
        for(int l : bankCode) {
            sum += l;
        }
        for(int l : accountNumber) {
            sum += l;
        }

        sum = sum % 100;

        if (sum < 10) {
            sum += 10;
        }

        char[] csm = sum.toString().toCharArray();
        checksum[0] = Integer.parseInt(String.valueOf(csm[0]));
        checksum[1] = Integer.parseInt(String.valueOf(csm[1]));
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        for(int i : checksum) {
            res.append(i);
        }

        Function<int[][], StringBuilder> prettyIBAN = (int[][] ars) -> {
            StringBuilder result = new StringBuilder();
            for (int[] ar: ars) {
                for(int i = 0; i < ar.length; i++) {
                    if(i % 4 == 0) {
                        result.append(" ");
                    }

                    result.append(ar[i]);
                }
            }
            return result;
        };

        res.append(prettyIBAN.apply(new int[][]{bankCode, accountNumber}));
        return res.toString();
    }
}
