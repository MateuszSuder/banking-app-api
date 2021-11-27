package banking.application.global.utils.Auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * UserAccounts POJO class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccounts {
    String standard;
    String multi;
    String crypto;

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getMulti() {
        return multi;
    }

    public void setMulti(String multi) {
        this.multi = multi;
    }

    public String getCrypto() {
        return crypto;
    }

    public void setCrypto(String crypto) {
        this.crypto = crypto;
    }

    @Override
    public String toString() {
        return "UserAccounts{" +
                "standard='" + standard + '\'' +
                ", multi='" + multi + '\'' +
                ", crypto='" + crypto + '\'' +
                '}';
    }
}
