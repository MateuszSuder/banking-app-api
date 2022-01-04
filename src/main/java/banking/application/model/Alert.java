package banking.application.model;

import java.util.Date;

public class Alert {
    String name;
    String value;
    Date date;

    public Alert(String name, String value) {
        this.name = name;
        this.value = value;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }
}
