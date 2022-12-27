package com.mtvu.usermanagementservice.api;

import com.mtvu.usermanagementservice.model.UserLoginType;
import com.mtvu.usermanagementservice.record.ChatUserDTO;
import com.mtvu.usermanagementservice.service.ChatUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * @author mvu
 * @project chat-socket
 **/
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserManagementController {
    private ChatUserService chatUserService;

    @PostMapping("/create/{user_type}")
    @PreAuthorize("hasAuthority('SCOPE_user:create')")
    public ResponseEntity<ChatUserDTO.Response.Public> register(@RequestBody ChatUserDTO.Request.Create userData,
                                                                @PathVariable("user_type") UserLoginType userType) {
        if (chatUserService.exists(userData.userId())) {
            return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
        }
        var isActivated = true;    // Todo: Need to verify email
        var user = chatUserService.createUser(userData, userType, isActivated);
        return ResponseEntity.ok(ChatUserDTO.Response.Public.create(user));
    }

    @GetMapping("/find")
    @PreAuthorize("hasAuthority('SCOPE_user:find')")
    public ResponseEntity<ChatUserDTO.Response.Public> findUser(@RequestHeader("FindUser") String username,
                                                                @RequestHeader("FindPwd")String password) {
        var user = chatUserService.findUser(username, password);
        return ResponseEntity.ok(ChatUserDTO.Response.Public.create(user));
    }

    @GetMapping("/current")
    @PreAuthorize("hasAuthority('SCOPE_profile:read')")
    public ResponseEntity<ChatUserDTO.Response.Public> findUser(Principal principal) {
        var user = chatUserService.getUser(principal.getName());
        return ResponseEntity.ok(ChatUserDTO.Response.Public.create(user));
    }

    @PutMapping("/password")
    @PreAuthorize("hasAuthority('SCOPE_profile:write')")
    public ResponseEntity<ChatUserDTO.Response.Public> changeUserPassword(Principal principal,
                                                                          ChatUserDTO.Request.Password password) {
        var user = chatUserService.getUser(principal.getName());
        user = chatUserService.updatePassword(user, password.newPassword());
        return ResponseEntity.ok(ChatUserDTO.Response.Public.create(user));
    }
}
