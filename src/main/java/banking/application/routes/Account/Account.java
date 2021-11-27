package banking.application.routes.Account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO class for Auth0 user
 * @JsonIgnoreProperties(ignoreUnknown = true) - Ignore nullish/unknown fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    public String user_id;
    public String email;
    public boolean email_verified;
    public String username;
    public String phone_number;
    public boolean phone_verified;
    public String created_at;
    public String updated_at;
    public Identity[] identities;
    public Metadata app_metadata;
    public Metadata user_metadata;
    public String picture;
    public String name;
    public String nickname;
    public String[] multifactor;
    public String last_ip;
    public String last_login;
    public long logins_count;
    public boolean blocked;
    public String given_name;
    public String family_name;
}

class Metadata {
    public String standard;
    public String multi;
    public String crypto;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Identity {
    public String connection;
    public String user_id;
    public String provider;
    public boolean isSocial;
}
