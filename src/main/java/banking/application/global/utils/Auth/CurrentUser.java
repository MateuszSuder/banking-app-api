package banking.application.global.utils.Auth;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Component containing current user.
 * Scope set to request. User is derived from jwt, but only for request containing this JWT.
 */
@Component
@RequestScope
public class CurrentUser {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "currentUser=" + currentUser +
                '}';
    }
}