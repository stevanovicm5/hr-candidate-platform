package com.intens.hr_platform.controller;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;
import com.intens.hr_platform.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/candidates")
@AllArgsConstructor
@Validated
@Tag(name = "Candidates", description = "Candidate management API")
public class CandidateController {

    private final CandidateService candidateService;

    @Operation(summary = "Get all candidates")
    @GetMapping
    public ResponseEntity<List<CandidateResponseDto>> getAllCandidates(){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.getAllCandidates());
    }

    @Operation(summary = "Add new candidate")
    @PostMapping
    public ResponseEntity<CandidateResponseDto> addCandidate(@Valid @RequestBody CandidateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addCandidate(request));
    }

    @Operation(summary = "Update candidate by ID")
    @PatchMapping("/{id}")
    public ResponseEntity<CandidateResponseDto> updateCandidate(@PathVariable Long id,
                                                                @Valid @RequestBody CandidateUpdateRequestDto request){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.updateCandidate(id, request));
    }

    @Operation(summary = "Delete candidate by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id){
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search candidates by name")
    @GetMapping("/search")
    public ResponseEntity<List<CandidateResponseDto>> searchByName(
            @RequestParam @NotBlank(message = "Name cannot be blank") String name){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.searchByName(name));
    }

    @Operation(summary = "Search candidates by skill")
    @GetMapping("/search/skills")
    public ResponseEntity<List<CandidateResponseDto>> searchBySkills(
            @RequestParam
            @NotEmpty(message = "At least one skill must be provided")
            List<@NotBlank(message = "Skill value cannot be blank") String> skills){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.searchBySkills(skills));
    }

    @Operation(summary = "Add skill to candidate")
    @PostMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto>addSkillToCandidate(@PathVariable Long candidateId,
                                                                   @PathVariable Long skillId){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.addSkillToCandidate(candidateId, skillId));
    }

    @Operation(summary = "Remove skill from candidate")
    @DeleteMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto> removeSkillFromCandidate(@PathVariable Long candidateId,
                                                                         @PathVariable Long skillId) {
        return ResponseEntity.ok(candidateService.removeSkillFromCandidate(candidateId, skillId));
    }

    @Operation(summary = "Search candidate by email")
    @GetMapping("/search/email")
    public ResponseEntity<CandidateResponseDto> searchByEmail(
            @RequestParam
            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Email format is invalid") String email){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.searchByEmail(email));
    }
}



