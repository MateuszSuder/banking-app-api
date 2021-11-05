package banking.application.global.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for pre-handler to validate jwt
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth { }
