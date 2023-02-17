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
@Builder(toBuilder = true)
@Table(name = "chat_user")
public class ChatUser {

    @Id
    @Column(name = "user_id", length = 127)
    private String userId;

    @Column(name = "user_login_type", nullable = false, updatable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserLoginType userLoginType;

    @Column(name = "password")
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Builder.Default
    @Column(name = "is_locked", columnDefinition = "boolean")
    private boolean isLocked = false;

    @Builder.Default
    @Column(name = "is_activated", columnDefinition = "boolean")
    private boolean isActivated = false;

    @OneToMany(mappedBy = "chatUser")
    private Set<ChatJoinRecord> chatJoinRecords;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "first_name", length = 127)
    private String firstName;

    @Column(name = "last_name", length = 127)
    private String lastName;
}
