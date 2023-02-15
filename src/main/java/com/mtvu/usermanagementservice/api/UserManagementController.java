package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.model.UserLoginType;
import com.mtvu.usermanagementservice.record.ChatUserDTO;
import com.mtvu.usermanagementservice.service.ChatUserService;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

/**
 * @author mvu
 * @project chat-socket
 **/
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/api/user")
public class UserManagementController {
    private ChatUserService chatUserService;

    private Principal principal;

    @POST
    @Path("/create/{user_type}")
    @RolesAllowed("profile:sys:create")
    public RestResponse<ChatUserDTO.Response.Public> register(ChatUserDTO.Request.Create userData,
                                                              @PathParam("user_type") UserLoginType userType) {
        if (chatUserService.exists(userData.userId())) {
            return RestResponse.status(Response.Status.CONFLICT);
        }
        var isActivated = true;    // Todo: Need to verify email
        var user = chatUserService.createUser(userData, userType, isActivated);
        return RestResponse.ok(ChatUserDTO.Response.Public.create(user));
    }

    @GET
    @Path("/find")
    @RolesAllowed("profile:sys:find")
    public RestResponse<ChatUserDTO.Response.Public> findUser(@HeaderParam("X-User-Credential") String credential) {
        String base64Credentials = credential.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        String username = credentials.split(":")[0];
        String password = credentials.split(":")[1];
        var user = chatUserService.findUser(username, password);
        return RestResponse.ok(ChatUserDTO.Response.Public.create(user));
    }

    @GET
    @Path("/get/{userId}")
    @RolesAllowed("profile:sys:find")
    public RestResponse<ChatUserDTO.Response.Public> getUser(@PathParam("userId") String userId) {
        var user = chatUserService.getUser(userId);
        return RestResponse.ok(ChatUserDTO.Response.Public.create(user));
    }

    @GET
    @Path("/current")
    @RolesAllowed("profile:user:read")
    public RestResponse<ChatUserDTO.Response.Public> findUser() {
        var user = chatUserService.getUser(principal.getName());
        return RestResponse.ok(ChatUserDTO.Response.Public.create(user));
    }

    @PUT
    @Path("/password")
    @RolesAllowed("profile:write")
    public RestResponse<ChatUserDTO.Response.Public> changeUserPassword(ChatUserDTO.Request.Password password) {
        var user = chatUserService.getUser(principal.getName());
        user = chatUserService.updatePassword(user, password.newPassword());
        return RestResponse.ok(ChatUserDTO.Response.Public.create(user));
    }
}
