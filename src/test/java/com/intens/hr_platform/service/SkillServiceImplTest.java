package com.intens.hr_platform.service;

import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.entity.Skill;
import com.intens.hr_platform.exception.DuplicateResourceException;
import com.intens.hr_platform.exception.ResourceNotFoundException;
import com.intens.hr_platform.mapper.SkillMapper;
import com.intens.hr_platform.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private Skill skill;
    private SkillRequestDto requestDto;
    private SkillResponseDto responseDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        requestDto = new SkillRequestDto();
        requestDto.setName("Java");

        responseDto = new SkillResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Java");
    }

    @Test
    void shouldAddSkill() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(skillMapper.toEntity(requestDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toResponseDto(skill)).thenReturn(responseDto);

        SkillResponseDto result = skillService.addSkill(requestDto);

        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateSkill() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> skillService.addSkill(requestDto));

        verify(skillRepository, never()).save(any());
        verifyNoInteractions(skillMapper);
    }

    @Test
    void shouldUpdateSkill() {
        SkillRequestDto updateDto = new SkillRequestDto();
        updateDto.setName("Spring");

        Skill updated = new Skill();
        updated.setId(1L);
        updated.setName("Spring");

        SkillResponseDto updatedResponse = new SkillResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setName("Spring");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.existsByNameIgnoreCase("Spring")).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(updated);
        when(skillMapper.toResponseDto(updated)).thenReturn(updatedResponse);

        SkillResponseDto result = skillService.updateSkill(1L, updateDto);

        assertNotNull(result);
        assertEquals("Spring", result.getName());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void shouldUpdateSkillWhenNameIsSameIgnoringCase() {
        SkillRequestDto updateDto = new SkillRequestDto();
        updateDto.setName("java");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toResponseDto(skill)).thenReturn(responseDto);

        SkillResponseDto result = skillService.updateSkill(1L, updateDto);

        assertNotNull(result);
        verify(skillRepository, never()).existsByNameIgnoreCase("java");
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDuplicateSkillName() {
        SkillRequestDto updateDto = new SkillRequestDto();
        updateDto.setName("Spring");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.existsByNameIgnoreCase("Spring")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> skillService.updateSkill(1L, updateDto));

        verify(skillRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentSkill() {
        SkillRequestDto updateDto = new SkillRequestDto();
        updateDto.setName("Spring");

        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> skillService.updateSkill(99L, updateDto));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void shouldDeleteSkill() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.deleteSkill(1L);

        verify(skillRepository, times(1)).delete(skill);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentSkill() {
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> skillService.deleteSkill(99L));
        verify(skillRepository, never()).delete(any());
    }

    @Test
    void shouldReturnAllSkills() {
        when(skillRepository.findAll()).thenReturn(List.of(skill));
        when(skillMapper.toResponseDto(skill)).thenReturn(responseDto);

        List<SkillResponseDto> result = skillService.getAllSkills();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.getFirst().getName());
        verify(skillRepository, times(1)).findAll();
    }
}
