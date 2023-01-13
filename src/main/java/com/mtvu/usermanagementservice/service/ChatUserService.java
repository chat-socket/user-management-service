package com.mtvu.usermanagementservice.service;

import com.mtvu.usermanagementservice.model.ChatUser;
import com.mtvu.usermanagementservice.model.UserLoginType;
import com.mtvu.usermanagementservice.record.ChatUserDTO;
import com.mtvu.usermanagementservice.repository.ChatUserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author mvu
 * @project chat-socket
 **/
@Service
@AllArgsConstructor
public class ChatUserService {

    private static Logger logger = LoggerFactory.getLogger(ChatUserService.class);

    private ChatUserRepository chatUserRepository;

    private PasswordEncoder passwordEncoder;

    public boolean exists(String userId) {
        return chatUserRepository.existsById(userId);
    }

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
        return chatUserRepository.save(chatUser);
    }

    public ChatUser findUser(String userId, String password) {
        var user = getUser(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.debug("Failed to authenticate since password does not match stored value");
            throw new UsernameNotFoundException(userId);
        }
        if (passwordEncoder.upgradeEncoding(user.getPassword())) {
            updatePassword(user, password);
        }

        return user;
    }

    public ChatUser getUser(String userId) {
        var chatUser = chatUserRepository.findById(userId);
        if (chatUser.isEmpty()) {
            throw new UsernameNotFoundException(userId);
        }
        return chatUser.get();
    }

    public ChatUser updatePassword(ChatUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return chatUserRepository.save(user);
    }
}
