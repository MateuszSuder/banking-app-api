package banking.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.Date;

@Document
public class SiteConfig {
    @Id
    String id;
    @Nullable
    Date lastAutoPayLoan;
    @Nullable
    Date lastCalculateInterest;

    @Nullable
    public Date getLastAutoPayLoan() {
        return lastAutoPayLoan;
    }

    @Nullable
    public Date getLastCalculateInterest() {
        return lastCalculateInterest;
    }

    public SiteConfig() {
        this.id = "GLOBAL";
    }

    public void setLastAutoPayLoan(@Nullable Date lastAutoPayLoan) {
        this.lastAutoPayLoan = lastAutoPayLoan;
    }

    public void setLastCalculateInterest(@Nullable Date lastCalculateInterest) {
        this.lastCalculateInterest = lastCalculateInterest;
    }
}
