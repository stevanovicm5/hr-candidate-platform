package com.intens.hr_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;
import com.intens.hr_platform.exception.DuplicateResourceException;
import com.intens.hr_platform.exception.ResourceNotFoundException;
import com.intens.hr_platform.service.CandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(CandidateController.class)
public class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidateService candidateService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private CandidateResponseDto responseDTO;
    private CandidateRequestDto requestDTO;
    private CandidateUpdateRequestDto updateRequestDTO;

    @BeforeEach
    void setUp() {
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

        updateRequestDTO = new CandidateUpdateRequestDto();
        updateRequestDTO.setFullName("Milan S.");
        updateRequestDTO.setEmail("milan.updated@example.com");
    }

    @Test
    void shouldReturnAllCandidates() throws Exception {
        when(candidateService.getAllCandidates()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Milan Stevanovic"))
                .andExpect(jsonPath("$[0].email").value("milan@example.com"));

        verify(candidateService, times(1)).getAllCandidates();
    }

    @Test
    void shouldAddCandidate() throws Exception {
        when(candidateService.addCandidate(any(CandidateRequestDto.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Milan Stevanovic"))
                .andExpect(jsonPath("$.email").value("milan@example.com"));

        verify(candidateService, times(1)).addCandidate(any(CandidateRequestDto.class));
    }

    @Test
    void shouldReturn400WhenAddingDuplicateCandidate() throws Exception {
        when(candidateService.addCandidate(any(CandidateRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Candidate already exists"));

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Candidate already exists"));
    }

    @Test
    void shouldReturn400WhenFullNameIsMissing() throws Exception {
        // null triggers only @NotBlank, avoiding overlap with @Size/@Pattern messages
        requestDTO.setFullName(null);

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").value("Full name is required"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn400WhenFullNameIsTooShort() throws Exception {
        requestDTO.setFullName("A");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fullName").value("Full name must be between 2 and 50 characters"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        requestDTO.setEmail("not-an-email");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email is not valid"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn400WhenDateOfBirthIsInvalid() throws Exception {
        requestDTO.setDateOfBirth(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dateOfBirth").value("Date of birth cannot be in the future"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn400WhenContactNumberIsInvalid() throws Exception {
        requestDTO.setContactNumber("invalid-contact");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.contactNumber").value("Contact number can only contain digits, spaces, hyphens, parentheses, and optional + prefix"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldUpdateCandidate() throws Exception {
        CandidateResponseDto updatedResponse = new CandidateResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setFullName("Milan S.");
        updatedResponse.setEmail("milan.updated@example.com");
        updatedResponse.setContactNumber("+381621234567");
        updatedResponse.setDateOfBirth(LocalDate.of(1995, 5, 15));
        updatedResponse.setSkills(new HashSet<>());

        when(candidateService.updateCandidate(eq(1L), any(CandidateUpdateRequestDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Milan S."))
                .andExpect(jsonPath("$.email").value("milan.updated@example.com"));
    }

    @Test
    void shouldReturn400WhenUpdatingWithInvalidEmail() throws Exception {
        updateRequestDTO.setEmail("invalid-email");

        mockMvc.perform(patch("/api/candidates/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email is not valid"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentCandidate() throws Exception {
        when(candidateService.updateCandidate(eq(99L), any(CandidateUpdateRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Candidate not found with id: 99"));

        mockMvc.perform(patch("/api/candidates/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Candidate not found with id: 99"));
    }

    @Test
    void shouldDeleteCandidate() throws Exception {
        mockMvc.perform(delete("/api/candidates/1"))
                .andExpect(status().isNoContent());

        verify(candidateService, times(1)).deleteCandidate(1L);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCandidate() throws Exception {
        doThrow(new ResourceNotFoundException("Candidate not found with id: 99"))
                .when(candidateService).deleteCandidate(99L);

        mockMvc.perform(delete("/api/candidates/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchCandidatesByName() throws Exception {
        when(candidateService.searchByName("Milan")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/candidates/search")
                        .param("name", "Milan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Milan Stevanovic"));

        verify(candidateService, times(1)).searchByName("Milan");
    }

    @Test
    void shouldReturn400WhenSearchNameIsBlank() throws Exception {
        mockMvc.perform(get("/api/candidates/search")
                        .param("name", ""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldSearchCandidatesBySkills() throws Exception {
        when(candidateService.searchBySkills(List.of("Java", "Spring"))).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/candidates/search/skills")
                        .param("skills", "Java", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Milan Stevanovic"));

        verify(candidateService, times(1)).searchBySkills(List.of("Java", "Spring"));
    }

    @Test
    void shouldReturn400WhenSkillsParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/candidates/search/skills"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldSearchCandidateByEmail() throws Exception {
        when(candidateService.searchByEmail("milan@example.com")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/candidates/search/email")
                        .param("email", "milan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("milan@example.com"));

        verify(candidateService, times(1)).searchByEmail("milan@example.com");
    }

    @Test
    void shouldReturn400WhenSearchEmailIsBlank() throws Exception {
        mockMvc.perform(get("/api/candidates/search/email")
                        .param("email", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email cannot be blank"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldReturn400WhenSearchEmailHasInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/candidates/search/email")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email format is invalid"));

        verifyNoInteractions(candidateService);
    }

    @Test
    void shouldAddSkillToCandidate() throws Exception {
        when(candidateService.addSkillToCandidate(1L, 1L)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/candidates/1/skills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Milan Stevanovic"));

        verify(candidateService, times(1)).addSkillToCandidate(1L, 1L);
    }

    @Test
    void shouldRemoveSkillFromCandidate() throws Exception {
        when(candidateService.removeSkillFromCandidate(1L, 1L)).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/candidates/1/skills/1"))
                .andExpect(status().isOk());

        verify(candidateService, times(1)).removeSkillFromCandidate(1L, 1L);
    }
}
