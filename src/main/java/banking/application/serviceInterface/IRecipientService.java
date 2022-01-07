package banking.application.serviceInterface;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.Recipient;

import java.util.List;

public interface IRecipientService {
    boolean recipientExists(String iban, String recipientIban);
    List<Recipient> getAllRecipients(String iban);
    List<Recipient> addRecipient(String iban, Recipient recipient) throws ThrowableErrorResponse;
    List<Recipient> deleteRecipient(String iban, String recipientIban) throws ThrowableErrorResponse;
    List<Recipient> modifyRecipient(String iban, Recipient recipient) throws ThrowableErrorResponse;

}
