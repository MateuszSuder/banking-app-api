package banking.application.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for pre-handler to validate jwt
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth { }
