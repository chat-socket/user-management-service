package com.mtvu.usermanagementservice.config;

import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.smallrye.jwt.build.Jwt;

import java.util.List;
import java.util.Set;

/**
 * @author mvu
 * @project chat-socket
 **/
public class OidcWiremockTestResourceConfig extends OidcWiremockTestResource {

    private static final String TOKEN_ISSUER = System.getProperty("quarkus.test.oidc.token.issuer",
            "https://server.example.com");
    private static final String TOKEN_AUDIENCE = System.getProperty("quarkus.test.oidc.token.audience",
            "https://server.example.com");


    public static String generateCustomJwtToken(String userName, List<String> scopes) {
        return Jwt.preferredUserName(userName)
                .issuer(TOKEN_ISSUER)
                .audience(TOKEN_AUDIENCE)
                .claim("sid", "session-id")
                .claim("scope", scopes)
                .subject("123456")
                .jws()
                .keyId("1")
                .sign("privateKey.jwk");
    }
}
