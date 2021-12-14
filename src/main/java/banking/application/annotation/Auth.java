package banking.application.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for pre-handler to validate jwt
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    // True if authorization code should be present in request
    boolean codeNeeded() default false;
}
