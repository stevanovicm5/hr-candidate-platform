package com.intens.hr_platform.service;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;
import com.intens.hr_platform.entity.Candidate;
import com.intens.hr_platform.entity.Skill;
import com.intens.hr_platform.exception.DuplicateResourceException;
import com.intens.hr_platform.exception.ResourceNotFoundException;
import com.intens.hr_platform.mapper.CandidateMapper;
import com.intens.hr_platform.repository.CandidateRepository;
import com.intens.hr_platform.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateServiceImpl implements CandidateService{

    private final CandidateRepository candidateRepository;
    private final SkillRepository skillRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public CandidateResponseDto addCandidate(CandidateRequestDto dto) {
        if (candidateRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new DuplicateResourceException("Candidate with email '" + dto.getEmail() + "' already exists.");
        }
        if (candidateRepository.existsByContactNumber(dto.getContactNumber())) {
            throw new DuplicateResourceException("Candidate with contact number '" + dto.getContactNumber() + "' already exists.");
        }

        Candidate candidate = candidateMapper.toEntity(dto);

        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            dto.getSkillIds().forEach(skillId -> {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));
                candidate.getSkills().add(skill);
            });
        }

        Candidate savedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toResponseDto(savedCandidate);
    }

    @Override
    public CandidateResponseDto updateCandidate(Long id, CandidateUpdateRequestDto dto) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        if (dto.getEmail() != null && candidateRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("Candidate with email '" + dto.getEmail() + "' already exists.");
        }

        if (dto.getContactNumber() != null && candidateRepository.existsByContactNumberAndIdNot(dto.getContactNumber(), id)) {
            throw new DuplicateResourceException("Candidate with contact number '" + dto.getContactNumber() + "' already exists.");
        }

        if (dto.getFullName() != null) {
            candidate.setFullName(dto.getFullName());
        }
        if (dto.getEmail() != null) {
            candidate.setEmail(dto.getEmail());
        }
        if (dto.getContactNumber() != null) {
            candidate.setContactNumber(dto.getContactNumber());
        }
        if (dto.getDateOfBirth() != null) {
            candidate.setDateOfBirth(dto.getDateOfBirth());
        }

        Candidate updatedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toResponseDto(updatedCandidate);
    }

    @Override
    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        candidateRepository.delete(candidate);
    }

    @Override
    public List<CandidateResponseDto> searchByName(String name) {
        return candidateRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(candidateMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<CandidateResponseDto> searchBySkills(List<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return List.of();
        }

        List<String> lowerCaseSkills = skillNames.stream()
                .map(String::toLowerCase)
                .toList();

        return candidateRepository.findBySkillNames(lowerCaseSkills)
                .stream()
                .map(candidateMapper::toResponseDto)
                .toList();
    }

    @Override
    public CandidateResponseDto addSkillToCandidate(Long candidateId, Long skillId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

        candidate.getSkills().add(skill);
        Candidate updatedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toResponseDto(updatedCandidate);
    }

    @Override
    public CandidateResponseDto removeSkillFromCandidate(Long candidateId, Long skillId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + candidateId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId));

        candidate.getSkills().remove(skill);
        Candidate updatedCandidate = candidateRepository.save(candidate);
        return candidateMapper.toResponseDto(updatedCandidate);
    }

    @Override
    public List<CandidateResponseDto> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(candidateMapper::toResponseDto)
                .toList();
    }

    @Override
    public CandidateResponseDto searchByEmail(String email){
        Candidate candidate = candidateRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with email: " + email));

        return candidateMapper.toResponseDto(candidate);
    }
}
