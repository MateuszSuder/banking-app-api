package banking.application.model;

import banking.application.util.AccountType;
import banking.application.util.IBAN;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    public ArrayList<String> getAllIBANs(){
        ArrayList<String> ibanList = new ArrayList<String>();
        if(this.isOpen(AccountType.standard)) {
            ibanList.add(this.getStandard());
        }
        if(this.isOpen(AccountType.multi)) {
            ibanList.add(this.getMulti());
        }
        if(this.isOpen(AccountType.crypto)) {
            ibanList.add(this.getCrypto());
        }
        return ibanList;
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
