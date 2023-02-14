package com.mtvu.usermanagementservice.repository;

import com.mtvu.usermanagementservice.model.ChatGroup;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author mvu
 * @project chat-socket
 **/
@ApplicationScoped
public class ChatGroupRepository implements PanacheRepositoryBase<ChatGroup, String> {
}
