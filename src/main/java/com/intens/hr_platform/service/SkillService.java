package com.intens.hr_platform.service;


import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;

import java.util.List;

public interface SkillService {
    SkillResponseDto addSkill(SkillRequestDto dto);
    SkillResponseDto updateSkill(Long id, SkillRequestDto dto);
    void deleteSkill(Long id);
    List<SkillResponseDto> getAllSkills();
}
