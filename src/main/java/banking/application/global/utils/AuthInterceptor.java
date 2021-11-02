package banking.application.global.utils;

import banking.application.global.classes.ErrorResponse;
import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    JwkProvider provider = new UrlJwkProvider("https://banking-application.eu.auth0.com");

    private void HTTPErrorResponse(HttpServletResponse r, String messageDetails) {
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

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        if(token == null) {
            HTTPErrorResponse(response, "No token found in Authorization header");
            return false;
        }

        try {
            token = token.replace("Bearer ", "");

            DecodedJWT jwt = JWT.decode(token);
            Jwk jwk = provider.get(jwt.getKeyId());

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://banking-application.eu.auth0.com/")
                    .build();

            verifier.verify(token);
        } catch (InvalidPublicKeyException e) {
            HTTPErrorResponse(response, "Invalid signature or claims");
            e.printStackTrace();
            return false;
        } catch (JWTDecodeException | JwkException e) {
            HTTPErrorResponse(response, "Invalid token");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
