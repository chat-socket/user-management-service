package com.mtvu.usermanagementservice.service;

import com.mtvu.usermanagementservice.model.ChatUser;
import com.mtvu.usermanagementservice.model.UserLoginType;
import com.mtvu.usermanagementservice.record.ChatUserDTO;
import com.mtvu.usermanagementservice.repository.ChatUserRepository;
import com.mtvu.usermanagementservice.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.HashSet;

/**
 * @author mvu
 * @project chat-socket
 **/
@ApplicationScoped
@AllArgsConstructor
public class ChatUserService {

    private static Logger logger = LoggerFactory.getLogger(ChatUserService.class);

    private ChatUserRepository chatUserRepository;

    private PasswordEncoder passwordEncoder;

    public boolean exists(String userId) {
        return chatUserRepository.count("userId", userId) > 0;
    }

    @Transactional
    public ChatUser createUser(ChatUserDTO.Request.Create newUser,
                               UserLoginType userLoginType, boolean isActivated) {
        var chatUser = ChatUser.builder()
            .userId(newUser.userId())
            .firstName(newUser.firstName())
            .lastName(newUser.lastName())
            .userLoginType(userLoginType)
            .password(passwordEncoder.encode(newUser.password()))
            .chatJoinRecords(new HashSet<>())
            .isActivated(isActivated)
            .avatar(newUser.avatar())
            .build();
        chatUserRepository.persist(chatUser);
        return chatUser;
    }

    public ChatUser findUser(String userId, String password) {
        var user = getUser(userId);
        if (!passwordEncoder.verify(password, user.getPassword())) {
            logger.debug("Failed to authenticate since password does not match stored value");
            throw new NotFoundException(userId);
        }

        return user;
    }

    public ChatUser getUser(String userId) {
        var chatUser = chatUserRepository.findById(userId);
        if (chatUser == null) {
            throw new NotFoundException(userId);
        }
        return chatUser;
    }

    public ChatUser updatePassword(ChatUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        chatUserRepository.persist(user);
        return user;
    }
}
