package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.model.*;
import com.mtvu.usermanagementservice.record.ChatGroupDTO;
import com.mtvu.usermanagementservice.service.ChatGroupService;
import com.mtvu.usermanagementservice.service.ChatUserService;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author mvu
 * @project chat-socket
 **/
@AllArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/api/group")
public class GroupManagementController {

    private final ChatGroupService chatGroupService;

    private final ChatUserService chatUserService;

    private Principal principal;

    /**
     * Get the details of a specific group by id
     * To protect the privacy of our user, this API is only accessible by system entities (group:sys:read)
     * @param groupId ID of the group
     * @return
     */
    @GET
    @Path("/{groupId}")
    @RolesAllowed("group:sys:read")
    public RestResponse<ChatGroupDTO.Response.Public> getGroup(@PathParam("groupId") String groupId) {
        var group = chatGroupService.getChatGroup(groupId);
        return group
            .map((x) -> RestResponse.ok(ChatGroupDTO.Response.Public.create(x)))
            .orElseGet(RestResponse::notFound);
    }

    /**
     * Get the list of the groups that the current user is participated.
     */
    @GET
    @RolesAllowed("group:user:read")
    public RestResponse<List<ChatGroupDTO.Response.Public>> getGroups() {
        var chatUser = chatUserService.getUser(principal.getName());
        var groups = chatUser.getChatJoinRecords().stream().map(ChatJoinRecord::getChatGroup).toList();
        return RestResponse.ok(groups.stream().map(ChatGroupDTO.Response.Public::create).toList());
    }

    @POST
    @RolesAllowed("group:user:write")
    public RestResponse<ChatGroupDTO.Response.Public> createGroup(ChatGroupDTO.Request.Create data) {

        if (!data.participants().contains(principal.getName())) {
            // The current user is supposed to be inside the list of participants
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }

        var chatUsers = new ArrayList<ChatUser>();
        for (String participant : data.participants()) {
            var chatUser = chatUserService.getUser(participant);
            chatUsers.add(chatUser);
        }

        var chatGroup = new ChatGroup();
        for (var chatUser : chatUsers) {
            var role = chatUser.getUserId().equals(principal.getName()) ? GroupRole.ADMIN : GroupRole.MEMBER;
            chatGroup.addMember(chatUser, role);
        }

        chatGroupService.createChatGroup(chatGroup);
        return RestResponse.ok(ChatGroupDTO.Response.Public.create(chatGroup));
    }
}
