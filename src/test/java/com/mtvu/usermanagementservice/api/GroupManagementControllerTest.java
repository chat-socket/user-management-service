package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.config.OidcWiremockTestResourceConfig;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResourceConfig.class)
class GroupManagementControllerTest {

    @Test
    public void whenProvidesAccessTokenWithInvalidAuthorityThenRejectTheRequest() {
        var username = "alice";
        var accessToken = OidcWiremockTestResourceConfig.getAccessToken(username, Set.of("user"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .when()
                .get("/api/group/{groupId}", "not-exists")
                .then()
                .statusCode(403);
    }

    @Test
    public void whenProvidesAccessTokenWithValidAuthorityThenAcceptTheRequest() {
        var username = "alice";
        var accessToken = OidcWiremockTestResourceConfig.generateCustomJwtToken(username,
                List.of("openid", "groups:read", "groups:write"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .when()
                .get("/api/group/{groupId}", "not-exists")
                .then()
                .statusCode(404);       // Group not found
    }
}