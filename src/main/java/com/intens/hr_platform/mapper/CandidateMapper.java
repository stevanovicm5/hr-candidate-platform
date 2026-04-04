package com.intens.hr_platform.mapper;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.entity.Candidate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CandidateMapper {

    private final SkillMapper skillMapper;

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

    public Candidate toEntity(CandidateRequestDto dto) {
        if(dto == null) {
            return null;
        }

        Candidate candidate = new Candidate();
        candidate.setFullName(dto.getFullName());
        candidate.setDateOfBirth(dto.getDateOfBirth());
        candidate.setEmail(dto.getEmail());
        candidate.setContactNumber(dto.getContactNumber());
        return candidate;
    }
}
