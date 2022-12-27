package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.model.ChatJoinRecord;
import com.mtvu.usermanagementservice.model.ChatUserGroupKey;
import com.mtvu.usermanagementservice.model.GroupRole;
import com.mtvu.usermanagementservice.record.ChatGroupDTO;
import com.mtvu.usermanagementservice.service.ChatGroupService;
import com.mtvu.usermanagementservice.service.ChatUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.ResponseEntity.notFound;

/**
 * @author mvu
 * @project chat-socket
 **/
@AllArgsConstructor
@RestController("/api/group")
public class GroupManagementController {

    private final ChatGroupService chatGroupService;

    private final ChatUserService chatUserService;

    @GetMapping("/{groupId}")
    @PreAuthorize("hasAuthority('SCOPE_groups:read')")
    public ResponseEntity<ChatGroupDTO.Response.Public> getGroup(@PathVariable("groupId") String groupId) {
        var group = chatGroupService.getChatGroup(groupId);
        return group
            .map((x) -> ResponseEntity.ok(ChatGroupDTO.Response.Public.create(x)))
            .orElseGet(() -> notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_groups:write')")
    public ResponseEntity<ChatGroupDTO.Response.Public> createGroup(
            @RequestBody ChatGroupDTO.Request.Create data, Principal principal) {
        var chatGroup = chatGroupService.createChatGroup(data);
        Set<ChatJoinRecord> chatJoinRecords = new HashSet<>();
        if (!data.participants().contains(principal.getName())) {
            // The current user is supposed to be inside the list of participants
            return ResponseEntity.badRequest().build();
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
        return ResponseEntity.ok(ChatGroupDTO.Response.Public.create(chatGroup));
    }
}
