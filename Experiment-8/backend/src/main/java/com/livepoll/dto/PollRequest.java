package com.livepoll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class PollRequest {
    @NotBlank
    private String question;

    @Size(min = 2, message = "Poll must have at least 2 options")
    private List<String> options;
}
