package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.config.OidcWiremockTestResourceConfig;
import com.mtvu.usermanagementservice.model.UserLoginType;
import com.mtvu.usermanagementservice.record.ChatGroupDTO;
import com.mtvu.usermanagementservice.record.ChatUserDTO;
import com.mtvu.usermanagementservice.service.ChatUserService;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResourceConfig.class)
class GroupManagementControllerTest {

    @Inject
    ChatUserService chatUserService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
                List.of("openid", "group:sys:read"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .when()
                .get("/api/group/{groupId}", "not-exists")
                .then()
                .statusCode(404);       // Group not found, as we haven't created it yet!
    }

    @Test
    public void whenCreateGroupWithNonExistingUserThenRejectTheRequest() throws JsonProcessingException {
        var username = "alice";
        var user = new ChatUserDTO.Request.Create(username, "Alice", "", "password", "");
        chatUserService.createUser(user, UserLoginType.EMAIL, true);

        var accessToken = OidcWiremockTestResourceConfig.generateCustomJwtToken(username,
                List.of("openid", "group:user:write"));

        var group = new ChatGroupDTO.Request.Create(Set.of(username, "bob"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(OBJECT_MAPPER.writeValueAsString(group))
                .when()
                .post("/api/group/create")
                .then()
                .statusCode(404);       // User bob not found
    }

    @Test
    public void whenCreateGroupWithValidUsersThenAcceptTheRequest() throws JsonProcessingException {
        var username = "alice";
        var user = new ChatUserDTO.Request.Create(username, "Alice", "", "password", "");
        chatUserService.createUser(user, UserLoginType.EMAIL, true);

        var user2 = new ChatUserDTO.Request.Create("bob", "Bob", "", "password", "");
        chatUserService.createUser(user2, UserLoginType.EMAIL, true);


        var accessToken = OidcWiremockTestResourceConfig.generateCustomJwtToken(username,
                List.of("openid", "group:user:write"));

        var group = new ChatGroupDTO.Request.Create(Set.of(username, "bob"));
        given()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(OBJECT_MAPPER.writeValueAsString(group))
                .when()
                .post("/api/group/create")
                .then()
                .statusCode(200);
    }
}