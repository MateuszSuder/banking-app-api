package banking.application.exception;

import banking.application.util.ErrorResponse;

// Class to throw Error Response class
public class ThrowableErrorResponse extends Exception {
    public int code;
    public String message = "";
    public String message_details = "";

    /**
     * Default constructor for creating simple HTTP Error response structure but throwable.
     * @see org.springframework.http.HttpStatus
     * @param message error message
     * @param message_details error details
     * @param code error code as number
     */
    public ThrowableErrorResponse(String message, String message_details, int code) {
        this.message = message;
        this.message_details = message_details;
        this.code = code;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(message, message_details, code);
    }
}
