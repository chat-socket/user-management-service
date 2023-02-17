package com.mtvu.usermanagementservice.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * @author mvu
 * @project chat-socket
 **/
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_join_record")
public class ChatJoinRecord {

    @EmbeddedId
    private ChatUserGroupKey userGroupKey;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private ChatGroup chatGroup;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private ChatUser chatUser;

    @Column(name = "joined_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime joinedAt;

    @Column(name = "role_id", nullable = false, updatable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    public ChatJoinRecord(ChatGroup chatGroup, ChatUser chatUser, GroupRole role) {
        this.chatGroup = chatGroup;
        this.chatUser = chatUser;
        this.userGroupKey = new ChatUserGroupKey(chatGroup.getGroupId(), chatUser.getUserId());
        this.role = role;
    }
}
