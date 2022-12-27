package com.mtvu.usermanagementservice.record;

import com.mtvu.usermanagementservice.model.ChatGroup;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mvu
 * @project chat-socket
 **/
public class ChatGroupDTO {
    public enum Request {;
        public record Create(Set<String> participants) {}
    }
    public enum Response {;
        public record Public(@NotBlank String groupId, String name, String description, String avatar,
                             Set<String> participants, OffsetDateTime createdAt) {
            public static Public create(ChatGroup chatGroup) {
                var participants = chatGroup.getChatJoinRecords().stream()
                    .map((x) -> x.getChatUser().getUserId())
                    .collect(Collectors.toSet());
                return new Public(
                    chatGroup.getGroupId(),
                    chatGroup.getGroupName(),
                    chatGroup.getGroupDescription(),
                    chatGroup.getGroupAvatar(),
                    participants,
                    chatGroup.getCreatedAt());
            }
        }
    }
}
