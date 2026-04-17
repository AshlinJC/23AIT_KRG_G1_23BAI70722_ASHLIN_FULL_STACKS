package com.livepoll.repository;

import com.livepoll.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
