package com.intens.hr_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.exception.DuplicateResourceException;
import com.intens.hr_platform.exception.ResourceNotFoundException;
import com.intens.hr_platform.service.SkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
public class SkillControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SkillService skillService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private SkillRequestDto requestDto;
	private SkillResponseDto responseDto;

	@BeforeEach
	void setUp() {
		requestDto = new SkillRequestDto();
		requestDto.setName("Java");

		responseDto = new SkillResponseDto();
		responseDto.setId(1L);
		responseDto.setName("Java");
	}

	@Test
	void shouldReturnAllSkills() throws Exception {
		when(skillService.getAllSkills()).thenReturn(List.of(responseDto));

		mockMvc.perform(get("/api/skills"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Java"));

		verify(skillService, times(1)).getAllSkills();
	}

	@Test
	void shouldAddSkill() throws Exception {
		when(skillService.addSkill(any(SkillRequestDto.class))).thenReturn(responseDto);

		mockMvc.perform(post("/api/skills")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Java"));

		verify(skillService, times(1)).addSkill(any(SkillRequestDto.class));
	}

	@Test
	void shouldReturn400WhenAddingDuplicateSkill() throws Exception {
		when(skillService.addSkill(any(SkillRequestDto.class)))
				.thenThrow(new DuplicateResourceException("Skill with name 'Java' already exists"));

		mockMvc.perform(post("/api/skills")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Skill with name 'Java' already exists"));
	}

	@Test
	void shouldReturn400WhenSkillNameIsMissing() throws Exception {
		requestDto.setName(null);

		mockMvc.perform(post("/api/skills")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Skill name is required"));

		verifyNoInteractions(skillService);
	}

	@Test
	void shouldReturn400WhenSkillNameIsTooShort() throws Exception {
		requestDto.setName("A");

		mockMvc.perform(post("/api/skills")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.name").value("Skill name must be between 2 and 50 characters"));

		verifyNoInteractions(skillService);
	}

	@Test
	void shouldUpdateSkill() throws Exception {
		SkillResponseDto updated = new SkillResponseDto();
		updated.setId(1L);
		updated.setName("Spring");

		SkillRequestDto updateRequest = new SkillRequestDto();
		updateRequest.setName("Spring");

		when(skillService.updateSkill(eq(1L), any(SkillRequestDto.class))).thenReturn(updated);

		mockMvc.perform(patch("/api/skills/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Spring"));

		verify(skillService, times(1)).updateSkill(eq(1L), any(SkillRequestDto.class));
	}

	@Test
	void shouldReturn400WhenUpdatingSkillWithDuplicateName() throws Exception {
		SkillRequestDto updateRequest = new SkillRequestDto();
		updateRequest.setName("Java");

		when(skillService.updateSkill(eq(1L), any(SkillRequestDto.class)))
				.thenThrow(new DuplicateResourceException("Skill with name 'Java' already exists"));

		mockMvc.perform(patch("/api/skills/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Skill with name 'Java' already exists"));
	}

	@Test
	void shouldReturn404WhenUpdatingNonExistentSkill() throws Exception {
		SkillRequestDto updateRequest = new SkillRequestDto();
		updateRequest.setName("Kotlin");

		when(skillService.updateSkill(eq(99L), any(SkillRequestDto.class)))
				.thenThrow(new ResourceNotFoundException("Skill not found with id: 99"));

		mockMvc.perform(patch("/api/skills/99")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Skill not found with id: 99"));
	}

	@Test
	void shouldDeleteSkill() throws Exception {
		mockMvc.perform(delete("/api/skills/1"))
				.andExpect(status().isNoContent());

		verify(skillService, times(1)).deleteSkill(1L);
	}

	@Test
	void shouldReturn404WhenDeletingNonExistentSkill() throws Exception {
		doThrow(new ResourceNotFoundException("Skill with id: 99 not found"))
				.when(skillService).deleteSkill(99L);

		mockMvc.perform(delete("/api/skills/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Skill with id: 99 not found"));

		verify(skillService, never()).getAllSkills();
	}
}
