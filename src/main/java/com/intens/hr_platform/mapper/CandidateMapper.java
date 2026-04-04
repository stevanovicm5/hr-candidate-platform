package com.intens.hr_platform.mapper;

import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.entity.Candidate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CandidateMapper {

    private SkillMapper skillMapper;

    public CandidateResponseDto toResponseDto(Candidate candidate){
        if(candidate == null){
            return null;
        }

        CandidateResponseDto dto = new CandidateResponseDto();
        dto.setId(candidate.getId());
        dto.setFullName(candidate.getFullName());
        dto.setDateOfBirth(candidate.getDateOfBirth());
        dto.setEmail(candidate.getEmail());
        dto.setContactNumber(candidate.getContactNumber());
        dto.setSkills(candidate.getSkills().stream()
                .map(skillMapper::toResponseDto)
                .collect(Collectors.toSet()));
        return dto;

    }
}
