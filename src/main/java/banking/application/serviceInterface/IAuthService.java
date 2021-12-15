package banking.application.serviceInterface;

import banking.application.model.Code;

/**
 * Interface for Auth Service
 */
public interface IAuthService {
    Code generateCodeForUser(String userID);
}
