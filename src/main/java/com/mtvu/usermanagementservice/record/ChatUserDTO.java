package com.mtvu.usermanagementservice.record;

import com.mtvu.usermanagementservice.model.ChatUser;
import com.mtvu.usermanagementservice.model.UserLoginType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * @author mvu
 * @project chat-socket
 **/
public enum ChatUserDTO {;
    public enum Request {;
        public record Create(@NotBlank String userId, String fullName, String password, String avatar) {}


        public record Password(@NotBlank String newPassword) {}
    }

    public enum Response {;
        public record Public(@NotBlank String userId, String fullName, @NotNull UserLoginType userLoginType,
                             String avatar, boolean isActivated, boolean isLocked, OffsetDateTime createdAt) {
            public static Public create(ChatUser chatUser) {
                return new Public(
                        chatUser.getUserId(),
                        chatUser.getFullName(),
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
