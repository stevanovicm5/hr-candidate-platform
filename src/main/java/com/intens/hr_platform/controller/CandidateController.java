package com.intens.hr_platform.controller;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;
import com.intens.hr_platform.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
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

    @Operation(summary = "Search candidates using flexible query parameters",
               description = "Search by name, email, or skills. Use only one parameter at a time. " +
                             "At least one search parameter must be provided.")
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            String email,
            @RequestParam(required = false)
            List<String> skills) {

        if (name != null && name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }

        if (email != null && email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email format is invalid");
        }

        if (skills != null) {
            if (skills.isEmpty()) {
                throw new IllegalArgumentException("At least one skill must be provided");
            }
            for (String skill : skills) {
                if (skill == null || skill.isBlank()) {
                    throw new IllegalArgumentException("Skill value cannot be blank");
                }
            }
        }

        if ((name == null || name.isBlank()) &&
            (email == null || email.isBlank()) &&
            skills == null) {
            throw new IllegalArgumentException("At least one search parameter (name, email, or skills) must be provided");
        }

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(candidateService.searchByName(name));
        }

        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(candidateService.searchByEmail(email));
        }

        if (skills != null) {
            return ResponseEntity.ok(candidateService.searchBySkills(skills));
        }

        return ResponseEntity.ok(List.of());
    }

    @Operation(summary = "Add skill to candidate")
    @PostMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto> addSkillToCandidate(@PathVariable Long candidateId,
                                                                    @PathVariable Long skillId){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.addSkillToCandidate(candidateId, skillId));
    }

    @Operation(summary = "Remove skill from candidate")
    @DeleteMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto> removeSkillFromCandidate(@PathVariable Long candidateId,
                                                                         @PathVariable Long skillId) {
        return ResponseEntity.ok(candidateService.removeSkillFromCandidate(candidateId, skillId));
    }
}



