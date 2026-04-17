package com.livepoll.controller;

import com.livepoll.entity.Poll;
import com.livepoll.entity.User;
import com.livepoll.service.PollService;
import com.livepoll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Entire controller locked to ADMIN role via class-level @PreAuthorize
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private PollService pollService;

    // GET /api/admin/users — list all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // GET /api/admin/polls — list ALL polls (including inactive)
    @GetMapping("/polls")
    public ResponseEntity<List<Poll>> getAllPolls() {
        return ResponseEntity.ok(pollService.getAll());
    }

    // GET /api/admin/stats — dashboard summary
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalPolls = pollService.getAll().size();
        long totalUsers = userService.findAll().size();
        long activePolls = pollService.getAllActive().size();
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("totalPolls", totalPolls);
            put("activePolls", activePolls);
            put("totalUsers", totalUsers);
        }});
    }
}
