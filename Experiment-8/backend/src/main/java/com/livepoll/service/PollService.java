package com.livepoll.service;

import com.livepoll.dto.PollRequest;
import com.livepoll.entity.*;
import com.livepoll.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PollService {

    @Autowired private PollRepository pollRepository;
    @Autowired private VoteRepository voteRepository;
    @Autowired private UserRepository userRepository;

    public List<Poll> getAllActive() {
        return pollRepository.findByActiveTrue();
    }

    public List<Poll> getAll() {
        return pollRepository.findAll();
    }

    public Poll getById(Long id) {
        return pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Poll not found: " + id));
    }

    public Poll create(PollRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion());
        poll.setCreatedBy(creator);

        request.getOptions().forEach(optText -> {
            PollOption option = new PollOption(optText);
            poll.getOptions().add(option);
        });

        return pollRepository.save(poll);
    }

    public void vote(Long pollId, Long optionId, String voterEmail) {
        User voter = userRepository.findByEmail(voterEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (voteRepository.existsByPollIdAndUserId(pollId, voter.getId())) {
            throw new RuntimeException("You have already voted on this poll");
        }

        Poll poll = getById(pollId);

        if (!poll.isActive()) {
            throw new RuntimeException("This poll is no longer active");
        }

        PollOption option = poll.getOptions().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Option not found: " + optionId));

        option.setVoteCount(option.getVoteCount() + 1);
        voteRepository.save(new Vote(poll, option, voter));
    }

    public Poll toggleActive(Long id) {
        Poll poll = getById(id);
        poll.setActive(!poll.isActive());
        return pollRepository.save(poll);
    }

    public void delete(Long id) {
        pollRepository.deleteById(id);
    }
}
