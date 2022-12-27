package com.mtvu.usermanagementservice.repository;

import com.mtvu.usermanagementservice.model.ChatGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mvu
 * @project chat-socket
 **/
@Repository
public interface ChatGroupRepository extends CrudRepository<ChatGroup, String> {
}
