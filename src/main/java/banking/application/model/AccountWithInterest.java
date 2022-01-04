package banking.application.model;

import org.springframework.data.annotation.Id;

public class AccountWithInterest {
    @Id
    String id;
    double interest;
    int loanId;

    public AccountWithInterest(String id, double interest, int loanId) {
        this.id = id;
        this.interest = interest;
        this.loanId = loanId;
    }

    public String getId() {
        return id;
    }

    public double getInterest() {
        return interest;
    }

    public int getLoanId() {
        return loanId;
    }
}
