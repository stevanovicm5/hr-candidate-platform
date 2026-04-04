package com.intens.hr_platform.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequestDto {

    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 50, message = "Skill name must be between 2 and 50 characters")
    @Pattern(regexp = "^\\S.*\\S$|^\\S$", message = "Skill name cannot start or end with whitespace")
    private String name;
}
