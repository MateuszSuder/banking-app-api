package banking.application.model;

import banking.application.util.AccountType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;

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

    public Boolean isOpen(AccountType accountType) {
        try {
            Field field = UserAccounts.class.getDeclaredField(String.valueOf(accountType));
            return field.get(this) != null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }

    public String getIban(AccountType accountType) {
        try {
            Field field = UserAccounts.class.getDeclaredField(String.valueOf(accountType));
            return field.get(this).toString();
        } catch (Exception e) {
            return null;
        }
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
