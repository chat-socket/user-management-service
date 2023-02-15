package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.model.ChatJoinRecord;
import com.mtvu.usermanagementservice.model.ChatUserGroupKey;
import com.mtvu.usermanagementservice.model.GroupRole;
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
import java.util.HashSet;
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

    @GET
    @Path("/{groupId}")
    @RolesAllowed({"groups:read"})
    public RestResponse<ChatGroupDTO.Response.Public> getGroup(@PathParam("groupId") String groupId) {
        var group = chatGroupService.getChatGroup(groupId);
        return group
            .map((x) -> RestResponse.ok(ChatGroupDTO.Response.Public.create(x)))
            .orElseGet(RestResponse::notFound);
    }

    @POST
    @Path("/create")
    @RolesAllowed("group:write")
    public RestResponse<ChatGroupDTO.Response.Public> createGroup(
            ChatGroupDTO.Request.Create data) {
        var chatGroup = chatGroupService.createChatGroup(data);
        Set<ChatJoinRecord> chatJoinRecords = new HashSet<>();
        if (!data.participants().contains(principal.getName())) {
            // The current user is supposed to be inside the list of participants
            return RestResponse.status(Response.Status.BAD_REQUEST);
        }
        for (String participant : data.participants()) {
            var role = participant.equals(principal.getName()) ? GroupRole.ADMIN : GroupRole.MEMBER;
            var chatUser = chatUserService.getUser(participant);
            chatJoinRecords.add(ChatJoinRecord.builder()
                    .id(new ChatUserGroupKey(chatGroup.getGroupId(), chatUser.getUserId()))
                    .chatGroup(chatGroup)
                    .chatUser(chatUser)
                    .role(role)
                    .build()
            );
        }
        chatGroupService.addChatMembers(chatGroup, chatJoinRecords);
        return RestResponse.ok(ChatGroupDTO.Response.Public.create(chatGroup));
    }
}
