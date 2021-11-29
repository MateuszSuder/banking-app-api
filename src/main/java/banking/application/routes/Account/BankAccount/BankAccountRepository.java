package banking.application.routes.Account.BankAccount;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {

    // Get account with codes only
    @Query(fields = "{'codes' : 1, '_id' : 0 }")
    BankAccount findItemById(String id);
}
