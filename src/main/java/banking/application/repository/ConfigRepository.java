package banking.application.repository;

import banking.application.model.SiteConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepository extends MongoRepository<SiteConfig, String> {
}
