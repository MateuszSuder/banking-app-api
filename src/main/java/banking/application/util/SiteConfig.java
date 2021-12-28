package banking.application.util;

/**
 * todo move it to database
 * Site config containing standard loan rates
 */
public final class SiteConfig {
    private final LoanConfig loanConfig;

    public SiteConfig() {
        this.loanConfig = new LoanConfig();
    }

    public LoanConfig getLoanConfig() {
        return loanConfig;
    }
}
