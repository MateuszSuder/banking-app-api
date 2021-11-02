package banking.application.global.classes;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    public int code;
    public String message = "";
    public String message_details = "";


    public ErrorResponse(String m1, String m2,  int code) {
        this.message = m1;
        this.message_details = m2;
        this.code = code;
    }
}
