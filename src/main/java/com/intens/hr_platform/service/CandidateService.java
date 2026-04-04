package com.intens.hr_platform.service;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;

import java.util.List;

public interface CandidateService {
    CandidateResponseDto addCandidate(CandidateRequestDto dto);
    CandidateResponseDto updateCandidate(Long id, CandidateUpdateRequestDto dto);
    void deleteCandidate(Long id);
    List<CandidateResponseDto> searchByName(String name);
    List<CandidateResponseDto> searchBySkills(List<String> skillNames);
    CandidateResponseDto searchByEmail(String email);
    CandidateResponseDto addSkillToCandidate(Long candidateId, Long skillId);
    CandidateResponseDto removeSkillFromCandidate(Long candidateId, Long skillId);
    List<CandidateResponseDto> getAllCandidates();
}
