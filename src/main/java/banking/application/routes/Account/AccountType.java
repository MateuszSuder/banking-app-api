package banking.application.routes.Account;

public enum AccountType {
    standard(1),
    multi(2),
    crypto(3);

    private final int value;

    AccountType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
