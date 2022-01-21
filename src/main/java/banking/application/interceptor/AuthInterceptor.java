package banking.application.interceptor;

import banking.application.model.Code;
import banking.application.util.ErrorResponse;
import banking.application.annotation.Auth;
import banking.application.model.User;
import banking.application.model.UserAccounts;
import banking.application.util.CurrentUser;
import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final Dotenv dotenv = Dotenv.load();
    ObjectMapper objectMapper = new ObjectMapper();
    /**
    URI for Jwk Provider
     @see <a href="http://auth0.com">Auth0.com</a>
     */
    JwkProvider provider = new UrlJwkProvider(dotenv.get("APP_DOMAIN"));

    // Field holding current user
    @Autowired
    CurrentUser currentUser;

    // Template for mongo operations
    @Autowired
    MongoTemplate mongoTemplate;

    // Autowired constructor
    @Autowired
    AuthInterceptor(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Handles HTTP errors changing original response. Overloaded. Shorter arguments syntax proxy data to proper method
     * @param r Response object
     * @param messageDetails Error details
     */
    private void HandleHTTPError(HttpServletResponse r, String messageDetails) {
        HandleHTTPError(r, "Unauthorized", messageDetails, 401);
    }

    /**
     * Handles HTTP errors changing original response, setting status to {@code status}, message to {@code message} and description to {@code messageDetails}
     * @param r Response object
     * @param messageDetails Error details
     */
    private void HandleHTTPError(HttpServletResponse r, String message, String messageDetails, int status) {
        ObjectMapper mapper = new ObjectMapper();
        r.setStatus(status);
        r.setContentType("application/json");
        try {
            r.getWriter()
                .write(mapper.
                    writeValueAsString(
                        new ErrorResponse(
                                message,
                                messageDetails,
                                status
                        )));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method handling code exchange
     * Checks if code exists and belongs to user sending request
     * Code should be sent in X-Code header
     * @param userID id of user requesting data
     * @param request current HTTP request
     * @param response current HTTP response
     * @return true if code is valid, false otherwise
     */
    private boolean isCodeAuthentic(String userID, HttpServletRequest request, HttpServletResponse response) {
        try {
            Integer code = Integer.parseInt(request.getHeader("X-Code"));

            Query query = new Query();
            query.addCriteria(new Criteria().andOperator(Criteria.where("code").is(code), Criteria.where("bindTo").is(userID)));
            Code result = mongoTemplate.findAndRemove(query, Code.class);

            if(result == null) {
                HandleHTTPError(response, "Unauthorized", "Authorization code is invalid", 401);
                return false;
            }

            return true;
        } catch (NullPointerException e) {
            HandleHTTPError(response, "Unauthorized", "Authorization code is missing", 401);
            return false;
        } catch (NumberFormatException e) {
            HandleHTTPError(response, "Unauthorized", "Authorization code is in invalid type", 401);
            return false;
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
            // Try to cast
            handlerMethod = (HandlerMethod) handler;
        } catch (ClassCastException e) {
            // TODO check if it's fine?
            return true;
        }

        // Get method
        Method method = handlerMethod.getMethod();

        // Try to get annotation
        Auth annotation = method.getAnnotation(Auth.class);

        // If no annotation skip authorization
        if (annotation == null) {
            return true;
        }

        // Get token
        String token = request.getHeader("Authorization");

        // If no token, refuse to pass
        if(token == null) {
            HandleHTTPError(response, "No token found in Authorization header");
            return false;
        }

        try {
            // Check for proper structure
            if(!token.contains("Bearer ")) {
                HandleHTTPError(response, "Invalid token structure");
                return false;
            }
            token = token.replace("Bearer ", "");

            // Validate JWT
            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(dotenv.get("APP_DOMAIN"))
                    .build();

            // Get decoded token
            DecodedJWT decoded = verifier.verify(token);

            // Decode token from base64 and then get payload
            String s = StringUtils.newStringUtf8(Base64.decodeBase64(decoded.getPayload()));
            JSONObject json = new JSONObject(s);

            // Get user from JWT's payload and set it to current user
            User u = objectMapper.readValue(json.get(dotenv.get("APP_JWT_NAMESPACE") + "user").toString(), User.class);

            // If authorization needed and code is not authentic discontinue request
//            if(annotation.codeNeeded() && !this.isCodeAuthentic(u.getUser_id(), request, response)) return false;
            try {
                UserAccounts ua = objectMapper.readValue(json.get(dotenv.get("APP_JWT_NAMESPACE") + "metadata").toString(), UserAccounts.class);
                u.setUserAccounts(ua);
            } catch (JSONException e) {
                u.setUserAccounts(new UserAccounts());
            }
            this.currentUser.setCurrentUser(u);
        } catch (InvalidPublicKeyException e) {
            HandleHTTPError(response, "Invalid signature or claims");
            return false;
        } catch (JWTDecodeException | JwkException e) {
            HandleHTTPError(response, "Invalid token");
            return false;
        } catch (TokenExpiredException e){
            HandleHTTPError(response, "Token expired");
            return false;
        } catch (JsonProcessingException e) {
            HandleHTTPError(response, "Internal error", "Error parsing client's token", 500);
            return false;
        }

        return true;
    }
}
