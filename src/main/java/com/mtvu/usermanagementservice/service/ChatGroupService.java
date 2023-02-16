package com.mtvu.usermanagementservice.service;

import com.mtvu.usermanagementservice.model.ChatGroup;
import com.mtvu.usermanagementservice.model.ChatJoinRecord;
import com.mtvu.usermanagementservice.repository.ChatGroupRepository;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.*;

/**
 * @author mvu
 * @project chat-socket
 **/
@ApplicationScoped
@AllArgsConstructor
public class ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;

    public Optional<ChatGroup> getChatGroup(String groupId) {
        return Optional.ofNullable(chatGroupRepository.findById(groupId));
    }

    private String generateGroupId(ChatGroup chatGroup) {
        if (chatGroup.getChatJoinRecords().size() == 2) {
            // For direct messaging
            var participants = chatGroup.getChatJoinRecords().stream().map(x -> x.getChatUser().getUserId()).toList();
            return "direct:" + String.join("|", participants.stream().sorted().toList());
        }
        return "group:" + UUID.randomUUID();
    }

    @Transactional
    public ChatGroup createChatGroup(ChatGroup chatGroup) {
        chatGroup.setGroupId(generateGroupId(chatGroup));
        chatGroupRepository.persist(chatGroup);
        return chatGroup;
    }
}
