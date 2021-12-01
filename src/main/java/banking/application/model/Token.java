package banking.application.model;

import java.sql.Timestamp;

/**
 * Auth0 JWT POJO class
 */
public class Token {
    public String access_token;
    public String scope;
    private long expires_in;
    private long expires_at;
    public String token_type;

    // Set Expires at instead, so we can know when it dies
    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        expires_at = expires_in * 1000 + timestamp.getTime();
    }

    public String getAccessToken() {
        return access_token;
    }

    public long getExpiresIn() {
        return expires_in;
    }

    public String getTokenType() {
        return token_type;
    }

    public long getExpiresAt() {
        return expires_at;
    }

    @Override
    public String toString() {
        return "Token{" +
                "accessToken='" + access_token + '\'' +
                ", scope='" + scope + '\'' +
                ", expiresAt=" + expires_in +
                ", tokenType='" + token_type + '\'' +
                '}';
    }
}
