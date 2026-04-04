package com.intens.hr_platform.mapper;

import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public SkillResponseDto toResponseDto(Skill skill) {
        if (skill == null) {
            return null;
        }
        SkillResponseDto dto = new SkillResponseDto();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        return dto;
    }

    public Skill toEntity(SkillRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setName(dto.getName());
        return skill;
    }
}
