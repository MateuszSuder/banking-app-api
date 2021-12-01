package banking.application.util;

/**
 * Class/Structure for handling HTTP Error responses.
 */
public class ErrorResponse{
    public int code;
    public String message = "";
    public String message_details = "";

    /**
     * Default constructor for creating simple HTTP Error response structure.
     * @see org.springframework.http.HttpStatus
     * @param message error message
     * @param message_details error details
     * @param code error code as number
     */
    public ErrorResponse(String message, String message_details, int code) {
        this.message = message;
        this.message_details = message_details;
        this.code = code;
    }
}


