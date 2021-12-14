package banking.application.service;

import banking.application.model.Code;
import banking.application.repository.CodeRepository;
import banking.application.serviceInterface.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for authorization purposes
 * Handles code generation
 */
@Service
public class AuthService implements IAuthService {
    @Autowired
    CodeRepository codeRepository;

    @Override
    public Code generateCodeForUser(String userID) {
        Code code = new Code(userID);
        this.codeRepository.save(code);

        return code;
    }
}
