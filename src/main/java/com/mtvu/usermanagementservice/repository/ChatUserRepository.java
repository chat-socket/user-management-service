package com.mtvu.usermanagementservice.repository;

import com.mtvu.usermanagementservice.model.ChatUser;
import org.springframework.data.repository.CrudRepository;

/**
 * @author mvu
 * @project chat-socket
 **/
public interface ChatUserRepository extends CrudRepository<ChatUser, String> {
}
