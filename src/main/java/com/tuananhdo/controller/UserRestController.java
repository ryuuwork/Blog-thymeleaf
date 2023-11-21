package com.tuananhdo.controller;

import com.tuananhdo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/admin/user/selected/delete")
    public ResponseEntity<Void> deleteSelectedUsers(@RequestBody List<Long> userIds) {
        userService.deleteSelectedUsers(userIds);
        return ResponseEntity.ok().build();
    }
}
