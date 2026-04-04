package com.intens.hr_platform.dto.candidate;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CandidateUpdateRequestDto {

    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    @Pattern(regexp = "^\\S.*\\S$|^\\S$", message = "Full name cannot start or end with whitespace")
    private String fullName;

    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    @Email(message = "Email is not valid")
    private String email;

    @Size(min = 9, max = 20, message = "Contact number must be between 9 and 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Contact number can only contain digits, spaces, hyphens, parentheses, and optional + prefix")
    private String contactNumber;
}
