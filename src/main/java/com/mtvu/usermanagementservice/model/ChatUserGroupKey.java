package com.mtvu.usermanagementservice.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author mvu
 * @project chat-socket
 **/
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatUserGroupKey implements Serializable {

    @Column(name = "group_id", length = 127)
    private String groupId;

    @Column(name = "user_id", length = 127)
    private String userId;
}
