package com.mtvu.usermanagementservice.record;

import com.mtvu.usermanagementservice.model.ChatUser;
import com.mtvu.usermanagementservice.model.UserLoginType;

import java.time.OffsetDateTime;

/**
 * @author mvu
 * @project chat-socket
 **/
public enum ChatUserDTO {;
    public enum Request {;
        public record Create(String userId, String firstName, String lastName, String password,
                             String avatar) {}


        public record Password(String newPassword) {}
    }

    public enum Response {;
        public record Public(String userId, String firstName, String lastName,
                             UserLoginType userLoginType, String avatar,
                             boolean isActivated, boolean isLocked, OffsetDateTime createdAt) {
            public static Public create(ChatUser chatUser) {
                return new Public(
                        chatUser.getUserId(),
                        chatUser.getFirstName(),
                        chatUser.getLastName(),
                        chatUser.getUserLoginType(),
                        chatUser.getAvatar(),
                        chatUser.isActivated(),
                        chatUser.isLocked(),
                        chatUser.getCreatedAt()
                );
            }
        }
    }
}
