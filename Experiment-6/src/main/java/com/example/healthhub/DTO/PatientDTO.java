package com.example.healthhub.DTO;  
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.*;
import lombok.Data;;

@Data
public class PatientDTO {

    private Long id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Min(value = 0, message = "Age cannot be negative")
    private int age;
    @NotNull(message = "Gender cannot be null")
    @NotEmpty(message = "Gender cannot be empty")
    private String gender;
    @NotNull(message = "Medical history cannot be null")
    @NotEmpty(message = "Medical history cannot be empty")
    private String medicalHistory;


}   