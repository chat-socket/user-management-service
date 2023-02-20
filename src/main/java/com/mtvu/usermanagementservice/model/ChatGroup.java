package com.mtvu.usermanagementservice.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mvu
 * @project chat-socket
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_group")
public class ChatGroup {

    @Id
    @Column(name = "group_id", nullable = false, length = 127)
    private String groupId;

    @Column(name = "group_name", nullable = false)
    private String groupName = "";

    @Column(name = "group_description", nullable = false, length = 1000)
    private String groupDescription = "";

    @Column(name = "group_avatar")
    private String groupAvatar = "";

    @OneToMany(mappedBy = "chatGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatJoinRecord> chatJoinRecords = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public void addMember(ChatUser chatUser, GroupRole role) {
        var chatJoinRecord = new ChatJoinRecord(this, chatUser, role);
        chatUser.getChatJoinRecords().add(chatJoinRecord);
        chatJoinRecords.add(chatJoinRecord);
    }
}
