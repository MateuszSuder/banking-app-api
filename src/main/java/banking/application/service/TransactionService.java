package banking.application.service;

import banking.application.model.Transaction;
import banking.application.model.input.TransactionPageInput;
import banking.application.model.output.PaginationOutput;
import banking.application.model.output.TransactionPageOutput;
import banking.application.serviceInterface.ITransactionService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Service handling transaction specific operations
 */
@Service
public class TransactionService extends EntryService implements ITransactionService {

    /**
     * Method to get transaction page with given filters for specific iban
     * @param ownerIBAN iban of account to get transactions
     * @param transactionPageInput filters and pagination
     * @return Transaction page with pagination
     */
    @Override
    public TransactionPageOutput getTransactionsPage(String ownerIBAN, TransactionPageInput transactionPageInput) {
        Query query = new Query();

        // Incoming/Outgoing transfers filter
        switch (transactionPageInput.getIoFilter()) {
            case INCOMING:
                query.addCriteria(new Criteria("receiverInfo.accountNumber").is(ownerIBAN));
                break;
            case OUTGOING:
                query.addCriteria(new Criteria("from").is(ownerIBAN));
                break;
            default:
                query.addCriteria(new Criteria().orOperator(Criteria.where("from").is(ownerIBAN), Criteria.where("receiverInfo.accountNumber").is(ownerIBAN)));
                break;
        }

        // Date filter
        if(transactionPageInput.getStartDate() != null && transactionPageInput.getEndDate() != null) {
            ObjectId startDate = new ObjectId(transactionPageInput.getStartDate());
            ObjectId endDate = new ObjectId(transactionPageInput.getEndDate());
            query.addCriteria(new Criteria("_id").gte(startDate).lte(endDate));
        } else {
            if(transactionPageInput.getStartDate() != null) {
                ObjectId startDate = new ObjectId(transactionPageInput.getStartDate());
                query.addCriteria(new Criteria("_id").gte(startDate));
            }
            if(transactionPageInput.getEndDate() != null) {
                ObjectId endDate = new ObjectId(transactionPageInput.getEndDate());
                query.addCriteria(new Criteria("_id").lte(endDate));
            }
        }

        // Recipient name filter
        if(transactionPageInput.getRecipientName() != null) {
            query.addCriteria(new Criteria("receiverInfo.recipientName").is(transactionPageInput.getRecipientName()));
        }

        // Recipient iban filter
        if(transactionPageInput.getRecipientIban() != null) {
            query.addCriteria(new Criteria("receiverInfo.accountNumber").is(transactionPageInput.getRecipientIban()));
        }

        // Amount filter
        if (transactionPageInput.getAmountFrom() != null && transactionPageInput.getAmountTo() != null) {
            query.addCriteria(new Criteria("sendValue.amount").gte(transactionPageInput.getAmountFrom()).lte(transactionPageInput.getAmountTo()));
        } else {
            if(transactionPageInput.getAmountFrom() != null) {
                query.addCriteria(new Criteria("sendValue.amount").gte(transactionPageInput.getAmountFrom()));
            }

            if(transactionPageInput.getAmountTo() != null) {
                query.addCriteria(new Criteria("sendValue.amount").lte(transactionPageInput.getAmountTo()));
            }
        }

        // Currency filter
        if(transactionPageInput.getCurrency() != null) {
            query.addCriteria(new Criteria("sendValue.currency").is(transactionPageInput.getCurrency()));
        }

        // Title filter
        if(transactionPageInput.getTitle() != null) {
            query.addCriteria(new Criteria("title").is(transactionPageInput.getTitle()));
        }

        // Transaction type filter
        if(transactionPageInput.getTransactionType() != null) {
            query.addCriteria(new Criteria("transactionType").in(transactionPageInput.getTransactionType()));
        }

        // Count for pagination
        long count = this.mongoTemplate.count(query, Transaction.class);

        // Sort by given sort type
        if(transactionPageInput.getSortType() != null) {
            switch(transactionPageInput.getSortType()) {
                case DATE_DESC:
                    query.with(Sort.by(Sort.Direction.DESC, "_id"));
                    break;
                case VALUE_ASC:
                    query.with(Sort.by(Sort.Direction.ASC, "sendValue.amount"));
                    break;
                case VALUE_DESC:
                    query.with(Sort.by(Sort.Direction.DESC, "sendValue.amount"));
                    break;
                default:
                    query.with(Sort.by(Sort.Direction.ASC, "_id"));
                    break;
            }
        }

        // Apply pagination
        query.skip(transactionPageInput.getPagination().getOffset()).limit(transactionPageInput.getPagination().getLimit());

        // Run query
        List<Transaction> result = this.mongoTemplate.find(query, Transaction.class);

        // Return results with pagination
        return new TransactionPageOutput(result, new PaginationOutput(
                transactionPageInput.getPagination().getOffset(),
                transactionPageInput.getPagination().getLimit(),
                count));
    }

    /**
     * Method that generate list of all transactions of given iban
     * @param iban account iban
     * @return list of transaction
     */
    public List<Transaction> getAllTransactions(String iban) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("from").is(iban), Criteria.where("receiverInfo.accountNumber").is(iban)));

        return this.mongoTemplate.find(query, Transaction.class);
    }

    /**
     * Method that generate all transactions to csv of given iban
     * @param iban iban of account to get transactions
     * @param response http response
     * @throws IOException error creating csv
     */
   public void bankStatementToCSV(String iban, HttpServletResponse response ) throws IOException {
       response.setContentType("text/csv");
       DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
       String currentDateTime = dateFormatter.format(new Date());
       String headerKey = "Content-Disposition";
       String headerValue = "attachment; filename=" + iban + "_" + currentDateTime + ".csv";
       response.setHeader(headerKey, headerValue);
       ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
       String[] csvHeader = {"From", "Receiver Number", "Receiver Name", "Title", "Send currency", "Send Amount", "Transaction type"};
       String[] nameMapping = {"from", "accountNumber", "recipientName", "title", "currency", "amount", "transactionType"};
       csvWriter.writeHeader(csvHeader);
       for(Transaction transaction: getAllTransactions(iban)) {
           csvWriter.write(transaction,nameMapping);
       }
       csvWriter.close();
    }
}
