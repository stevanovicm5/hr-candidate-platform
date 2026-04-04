package com.intens.hr_platform.dto.candidate;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonPropertyOrder({ "id", "fullName", "dateOfBirth", "email", "contactNumber", "skills" })
public class CandidateResponseDto {
    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private String email;
    private String contactNumber;
    private Set<SkillResponseDto> skills;
}
