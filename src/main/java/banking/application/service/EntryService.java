package banking.application.service;

import banking.application.repository.BankAccountRepository;
import banking.application.repository.CodeRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EntryService {
    // Local envs
    protected static final Dotenv dotenv = Dotenv.load();

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    CodeRepository codeRepository;
}
