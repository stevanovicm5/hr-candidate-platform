package com.intens.hr_platform.service;

import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.entity.Skill;
import com.intens.hr_platform.exception.DuplicateResourceException;
import com.intens.hr_platform.exception.ResourceNotFoundException;
import com.intens.hr_platform.mapper.SkillMapper;
import com.intens.hr_platform.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService{

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillResponseDto addSkill(SkillRequestDto dto) {
        if (skillRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new DuplicateResourceException("Skill with name '" + dto.getName() + "' already exists");
        }
        Skill skill = skillMapper.toEntity(dto);
        Skill savedSkill = skillRepository.save(skill);

        return skillMapper.toResponseDto(savedSkill);
    }

    @Override
    public SkillResponseDto updateSkill(Long id, SkillRequestDto dto) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(skill.getName())) {
            if (skillRepository.existsByNameIgnoreCase(dto.getName())) {
                throw new DuplicateResourceException("Skill with name '" + dto.getName() + "' already exists");
            }
        }
        skill.setName(dto.getName());
        Skill updatedSkill = skillRepository.save(skill);

        return skillMapper.toResponseDto(updatedSkill);

    }

    @Override
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill with id: " + id + " not found"));
        skillRepository.delete(skill);
    }

    @Override
    public List<SkillResponseDto> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(skillMapper::toResponseDto)
                .toList();
    }
}
