package banking.application.repository;

import banking.application.model.Code;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for codes collection
 */
public interface CodeRepository extends MongoRepository<Code, Integer> {
    Long deleteCodeByCode(Integer code);
}
