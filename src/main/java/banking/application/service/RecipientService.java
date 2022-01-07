package banking.application.service;

import banking.application.exception.ThrowableErrorResponse;
import banking.application.model.BankAccount;
import banking.application.model.Recipient;
import banking.application.serviceInterface.IRecipientService;
import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service handling recipient operations
 */
@Service
public class RecipientService extends EntryService implements IRecipientService {

    /**
     * Method checks if recipient exists for given account
     * @param iban account's IBAN
     * @param recipientIban recipient's IBAN
     * @return true if recipient exists, else false
     */
    @Override
    public boolean recipientExists(String iban, String recipientIban) {
        Optional<BankAccount> bankAccount = this.bankAccountRepository.findAccountByIdAndRecipient(iban, recipientIban);
        return bankAccount.isPresent();
    }

    /**
     * Get list of recipients for given account
     * @param iban account's IBAN
     * @return list of recipients
     */
    @Override
    public List<Recipient> getAllRecipients(String iban) {
        return this.bankAccountRepository.getListOfRecipients(iban);
    }

    /**
     * Add new recipient
     * @param iban account's IBAN
     * @param recipient recipient's entity
     * @return list of recipients
     * @throws ThrowableErrorResponse if account exists
     */
    @Override
    public List<Recipient> addRecipient(String iban, Recipient recipient) throws ThrowableErrorResponse {
        if (recipientExists(iban, recipient.getAccountNumber())) throw new ThrowableErrorResponse(
                "Conflict",
                "Recipient already exists",
                409);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(iban));
        Update update = new Update();
        update.push("savedRecipients", recipient);
        this.mongoTemplate.updateFirst(query, update, BankAccount.class);
        return this.getAllRecipients(iban);
    }

    /**
     * Delete recipient
     * @param iban account's IBAN
     * @param recipientIban recipient's IBAN
     * @return list of recipients
     * @throws ThrowableErrorResponse if recipient doesn't exist
     */
    @Override
    public List<Recipient> deleteRecipient(String iban, String recipientIban) throws ThrowableErrorResponse {
        if (!recipientExists(iban, recipientIban)) throw new ThrowableErrorResponse(
                "Not found",
                "Recipient doesn't exist",
                404);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(iban));
        Update update = new Update();
        update.pull("savedRecipients", new BasicDBObject("accountNumber", recipientIban));
        this.mongoTemplate.updateFirst(query, update, BankAccount.class);
        return this.getAllRecipients(iban);
    }

    /**
     * Modify recipient
     * @param iban account's IBAN
     * @param recipient recipient's entity
     * @return list of recipients
     * @throws ThrowableErrorResponse if recipient doesn't exist
     */
    @Override
    public List<Recipient> modifyRecipient(String iban, Recipient recipient) throws ThrowableErrorResponse {
        if (!recipientExists(iban, recipient.getAccountNumber())) throw new ThrowableErrorResponse(
                "Not found",
                "Recipient doesn't exists",
                404);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(iban));
        query.addCriteria(Criteria.where("savedRecipients.accountNumber").is(recipient.getAccountNumber()));
        Update update = new Update();
        update.set("savedRecipients.$.recipientName", recipient.getRecipientName());
        this.mongoTemplate.updateFirst(query, update, BankAccount.class);
        return this.getAllRecipients(iban);
    }
}
