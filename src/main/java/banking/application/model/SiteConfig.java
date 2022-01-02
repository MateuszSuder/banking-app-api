package banking.application.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.Date;

@Document
public class SiteConfig {
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

    public SiteConfig(@Nullable Date lastAutoPayLoan, @Nullable Date lastCalculateInterest) {
        this.lastAutoPayLoan = lastAutoPayLoan;
        this.lastCalculateInterest = lastCalculateInterest;
    }
}
