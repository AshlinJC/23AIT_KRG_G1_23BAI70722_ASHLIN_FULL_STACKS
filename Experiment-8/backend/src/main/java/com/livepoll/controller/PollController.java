package com.livepoll.controller;

import com.livepoll.dto.PollRequest;
import com.livepoll.entity.Poll;
import com.livepoll.service.PollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    @Autowired private PollService pollService;

    // GET /api/polls — public, no auth needed
    @GetMapping
    public ResponseEntity<List<Poll>> getActivePolls() {
        return ResponseEntity.ok(pollService.getAllActive());
    }

    // GET /api/polls/{id} — public
    @GetMapping("/{id}")
    public ResponseEntity<Poll> getPoll(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.getById(id));
    }

    // POST /api/polls — ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Poll> createPoll(
            @Valid @RequestBody PollRequest request,
            Authentication authentication) {
        Poll poll = pollService.create(request, authentication.getName());
        return ResponseEntity.ok(poll);
    }

    // POST /api/polls/{id}/vote — authenticated users (USER or ADMIN)
    @PostMapping("/{pollId}/vote")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> vote(
            @PathVariable Long pollId,
            @RequestParam Long optionId,
            Authentication authentication) {
        pollService.vote(pollId, optionId, authentication.getName());
        return ResponseEntity.ok("Vote recorded");
    }

    // PUT /api/polls/{id}/toggle — ADMIN only
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Poll> togglePoll(@PathVariable Long id) {
        return ResponseEntity.ok(pollService.toggleActive(id));
    }

    // DELETE /api/polls/{id} — ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        pollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
