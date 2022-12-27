package com.mtvu.usermanagementservice.service;

import com.mtvu.usermanagementservice.model.ChatGroup;
import com.mtvu.usermanagementservice.model.ChatJoinRecord;
import com.mtvu.usermanagementservice.record.ChatGroupDTO;
import com.mtvu.usermanagementservice.repository.ChatGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author mvu
 * @project chat-socket
 **/
@Service
@AllArgsConstructor
public class ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;

    public Optional<ChatGroup> getChatGroup(String groupId) {
        return chatGroupRepository.findById(groupId);
    }

    public String generateGroupId(List<String> participants) {
        if (participants.size() == 2) {
            Collections.sort(participants);
            return "direct:" + String.join("--", participants);
        }
        return "group:" + UUID.randomUUID();
    }

    public ChatGroup addChatMembers(ChatGroup chatGroup, Set<ChatJoinRecord> chatJoinRecords) {
        chatGroup.getChatJoinRecords().addAll(chatJoinRecords);
        return chatGroupRepository.save(chatGroup);
    }

    public ChatGroup createChatGroup(ChatGroupDTO.Request.Create data) {
        var chatGroup = ChatGroup.builder()
                .groupId(generateGroupId(data.participants().stream().toList()))
                .groupAvatar("")
                .groupDescription("")
                .groupName("")
                .chatJoinRecords(new HashSet<>())
                .build();
        return chatGroupRepository.save(chatGroup);
    }
}
