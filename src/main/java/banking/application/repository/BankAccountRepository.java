package banking.application.repository;

import banking.application.model.BankAccount;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {

    // Get account with codes only
    @Query(fields = "{'codes' : 1, '_id' : 0 }")
    BankAccount findItemById(String id);

    // Get account with currency only
    @Query(fields = "{'codes' : 1, '_id' : 0 }")
    BankAccount findCurrencyById(String id);

    @Aggregation(pipeline = {
            "{ $match: { _id : '78280350220130866382350461' }}",
            "{ $project: { _id: 0, lastLoan: { $last: '$loans' }}}",
            "{ $project: { lastInstallment: { $last: '$lastLoan.installments' }}}",
            "{ $project: { isActive : { $toBool: '$lastInstallment.amountLeftToPay' }}}"
    })
    Optional<Boolean> checkIfAccountIsActive(String iban);
}


