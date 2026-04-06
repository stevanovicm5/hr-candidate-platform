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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private CandidateMapper candidateMapper;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private Candidate candidate;
    private CandidateRequestDto requestDTO;
    private CandidateResponseDto responseDTO;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setFullName("Milan Stevanovic");
        candidate.setEmail("milan@example.com");
        candidate.setContactNumber("+381621234567");
        candidate.setDateOfBirth(LocalDate.of(1995, 5, 15));
        candidate.setSkills(new HashSet<>());

        requestDTO = new CandidateRequestDto();
        requestDTO.setFullName("Milan Stevanovic");
        requestDTO.setEmail("milan@example.com");
        requestDTO.setContactNumber("+381621234567");
        requestDTO.setDateOfBirth(LocalDate.of(1995, 5, 15));

        responseDTO = new CandidateResponseDto();
        responseDTO.setId(1L);
        responseDTO.setFullName("Milan Stevanovic");
        responseDTO.setEmail("milan@example.com");
        responseDTO.setContactNumber("+381621234567");
        responseDTO.setDateOfBirth(LocalDate.of(1995, 5, 15));
        responseDTO.setSkills(new HashSet<>());
    }

    @Test
    void shouldAddCandidate(){
        when(candidateMapper.toEntity(requestDTO)).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        CandidateResponseDto result = candidateService.addCandidate(requestDTO);

        assertNotNull(result);
        assertEquals("Milan Stevanovic", result.getFullName());
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void shouldAddCandidateWithoutInitialSkills() {
        requestDTO.setSkillIds(List.of());

        when(candidateMapper.toEntity(requestDTO)).thenReturn(candidate);
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        CandidateResponseDto result = candidateService.addCandidate(requestDTO);

        assertNotNull(result);
        verifyNoInteractions(skillRepository);
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void shouldThrowExceptionWhenAddingCandidateWithDuplicateEmail() {
        when(candidateRepository.existsByEmailIgnoreCase(requestDTO.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> candidateService.addCandidate(requestDTO));

        verify(candidateRepository, never()).save(any());
        verifyNoInteractions(skillRepository, candidateMapper);
    }

    @Test
    void shouldThrowExceptionWhenAddingCandidateWithDuplicateContactNumber() {
        when(candidateRepository.existsByEmailIgnoreCase(requestDTO.getEmail())).thenReturn(false);
        when(candidateRepository.existsByContactNumber(requestDTO.getContactNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> candidateService.addCandidate(requestDTO));

        verify(candidateRepository, never()).save(any());
        verifyNoInteractions(skillRepository, candidateMapper);
    }

    @Test
    void shouldThrowExceptionWhenAddingCandidateWithNonExistentSkillId() {
        requestDTO.setSkillIds(List.of(99L));
        when(candidateMapper.toEntity(requestDTO)).thenReturn(candidate);
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.addCandidate(requestDTO));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllCandidates(){
        when(candidateRepository.findAll()).thenReturn(List.of(candidate));
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        List<CandidateResponseDto> result = candidateService.getAllCandidates();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(candidateRepository, times(1)).findAll();
    }

    @Test
    void shouldDeleteCandidate(){
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(1L);

        verify(candidateRepository, times(1)).delete(candidate);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCandidate() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.deleteCandidate(99L));
        verify(candidateRepository, never()).deleteById(any());
    }

    @Test
    void shouldSearchCandidatesByName() {
        when(candidateRepository.findByFullNameContainingIgnoreCase("Milan"))
                .thenReturn(List.of(candidate));
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        List<CandidateResponseDto> result = candidateService.searchByName("Milan");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Milan Stevanovic", result.getFirst().getFullName());
    }

    @Test
    void shouldSearchCandidatesBySingleSkillUsingExactMatch() {
        when(candidateRepository.findBySkillNames(List.of("java"))).thenReturn(List.of(candidate));
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        List<CandidateResponseDto> result = candidateService.searchBySkills(List.of("Java"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(candidateRepository, times(1)).findBySkillNames(List.of("java"));
    }

    @Test
    void shouldSearchCandidatesByMultipleSkillsUsingLowercaseNames() {
        when(candidateRepository.findBySkillNames(List.of("java", "spring"))).thenReturn(List.of(candidate));
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        List<CandidateResponseDto> result = candidateService.searchBySkills(List.of("Java", "Spring"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(candidateRepository, times(1)).findBySkillNames(List.of("java", "spring"));
    }

    @Test
    void shouldSearchCandidateByEmail() {
        when(candidateRepository.findByEmailIgnoreCase("milan@example.com")).thenReturn(Optional.of(candidate));
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        CandidateResponseDto result = candidateService.searchByEmail("milan@example.com");

        assertNotNull(result);
        assertEquals("milan@example.com", result.getEmail());
        verify(candidateRepository, times(1)).findByEmailIgnoreCase("milan@example.com");
    }

    @Test
    void shouldThrowExceptionWhenSearchingByEmailAndCandidateDoesNotExist() {
        when(candidateRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> candidateService.searchByEmail("unknown@example.com"));
        verifyNoInteractions(candidateMapper);
    }

    @Test
    void shouldAddSkillToCandidate() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        CandidateResponseDto result = candidateService.addSkillToCandidate(1L, 1L);

        assertNotNull(result);
        assertTrue(candidate.getSkills().contains(skill));
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void shouldThrowExceptionWhenAddingSkillToNonExistentCandidate() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.addSkillToCandidate(99L, 1L));
    }

    @Test
    void shouldThrowExceptionWhenAddingNonExistentSkillToCandidate() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.addSkillToCandidate(1L, 99L));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldRemoveSkillFromCandidate() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");
        candidate.getSkills().add(skill);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        candidateService.removeSkillFromCandidate(1L, 1L);

        assertFalse(candidate.getSkills().contains(skill));
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void shouldThrowExceptionWhenRemovingSkillFromNonExistentCandidate() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.removeSkillFromCandidate(99L, 1L));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentSkillFromCandidate() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.removeSkillFromCandidate(1L, 99L));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCandidate() {
        CandidateUpdateRequestDto updateDTO = new CandidateUpdateRequestDto();
        updateDTO.setFullName("Slobodan Stevanovic");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateMapper.toResponseDto(candidate)).thenReturn(responseDTO);

        CandidateResponseDto result = candidateService.updateCandidate(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Slobodan Stevanovic", candidate.getFullName());
        verify(candidateRepository, times(1)).save(candidate);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCandidateWithDuplicateEmail() {
        CandidateUpdateRequestDto updateDTO = new CandidateUpdateRequestDto();
        updateDTO.setEmail("duplicate@example.com");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.existsByEmailIgnoreCaseAndIdNot("duplicate@example.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> candidateService.updateCandidate(1L, updateDTO));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCandidateWithDuplicateContactNumber() {
        CandidateUpdateRequestDto updateDTO = new CandidateUpdateRequestDto();
        updateDTO.setContactNumber("+381631111111");

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.existsByContactNumberAndIdNot("+381631111111", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> candidateService.updateCandidate(1L, updateDTO));

        verify(candidateRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCandidate() {
        CandidateUpdateRequestDto updateDTO = new CandidateUpdateRequestDto();
        updateDTO.setFullName("Slobodan Stevanovic");

        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.updateCandidate(99L, updateDTO));
    }
}
