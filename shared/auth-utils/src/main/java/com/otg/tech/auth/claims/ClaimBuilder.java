package com.otg.tech.auth.claims;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClaimBuilder {

    private static final String SESSION_STATE_ID = "sid";

    private ClaimBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static Identity Identity(String authToken) {
        DecodedJWT decodedJWT = JWT.decode(authToken);
        Identity identity = new Identity();
        identity.setSessionStateId(decodedJWT.getClaim(SESSION_STATE_ID).asString());
        return identity;
    }
}
