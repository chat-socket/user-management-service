package com.mtvu.usermanagementservice.api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.Set;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@QuarkusTestResource(OidcWiremockTestResource.class)
class GroupManagementControllerTest {

    @Test
    public void whenProvidesAccessTokenWithInvalidAuthorityThenRejectTheRequest() {
        var username = "alice";
        var accessToken = OidcWiremockTestResource.getAccessToken(username, Set.of("user"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .when()
                .get("/api/group/{username}", "alice")
                .then()
                .statusCode(401);
    }
}