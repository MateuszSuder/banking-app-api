package banking.application.global.utils;

import banking.application.global.classes.ErrorResponse;
import banking.application.global.interfaces.Auth;
import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.interfaces.RSAPublicKey;

/**
Interceptor checking presence and correctness of JWT Token for methods annotated with {@link Auth @Auth}.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    /**
    URI for Jwk Provider
     @see <a href="http://auth0.com">Auth0.com</a>
     */
    JwkProvider provider = new UrlJwkProvider("https://banking-application.eu.auth0.com");

    /**
     * Handles HTTP errors changing original response, setting status to {@code 401}, message to {@code Unauthorized} and description to {@code messageDetails}
     * @param r Response object
     * @param messageDetails Error details
     */
    private void HandleHTTPError(HttpServletResponse r, String messageDetails) {
        ObjectMapper mapper = new ObjectMapper();
        r.setStatus(401);
        r.setContentType("application/json");
        try {
            r.getWriter()
                .write(mapper.
                    writeValueAsString(
                        new ErrorResponse(
                            "Unauthorized",
                            messageDetails,
                            401
                        )));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pre-handler for authorization/authentication. Checks if endpoint need authorization ({@link Auth @Auth} annotation). If it does authorize client.
     * For invalid/absent tokens returns HTTP response with 401 status code.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html">Handler Interceptor documentation</a>
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler itself. Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (ClassCastException e) {
            return preHandle(request, response, handler);
        }

        Method method = handlerMethod.getMethod();

        if (!method.isAnnotationPresent(Auth.class)) {
            return true;
        }


        String token = request.getHeader("Authorization");

        if(token == null) {
            HandleHTTPError(response, "No token found in Authorization header");
            return false;
        }

        try {
            if(!token.contains("Bearer ")) {
                HandleHTTPError(response, "Invalid token structure");
                return false;
            }
            token = token.replace("Bearer ", "");

            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://banking-application.eu.auth0.com/")
                    .build();

            verifier.verify(token);
        } catch (InvalidPublicKeyException e) {
            HandleHTTPError(response, "Invalid signature or claims");
            e.printStackTrace();
            return false;
        } catch (JWTDecodeException | JwkException e) {
            HandleHTTPError(response, "Invalid token");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
