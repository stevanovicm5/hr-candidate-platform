package com.intens.hr_platform.controller;

import com.intens.hr_platform.dto.candidate.CandidateRequestDto;
import com.intens.hr_platform.dto.candidate.CandidateResponseDto;
import com.intens.hr_platform.dto.candidate.CandidateUpdateRequestDto;
import com.intens.hr_platform.service.CandidateService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/candidates")
@AllArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<CandidateResponseDto>> getAllCandidates(){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.getAllCandidates());
    }

    @PostMapping
    public ResponseEntity<CandidateResponseDto> addCandidate(@Valid @RequestBody CandidateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.addCandidate(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CandidateResponseDto> updateCandidate(@PathVariable Long id,
                                                                @Valid @RequestBody CandidateUpdateRequestDto request){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.updateCandidate(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id){
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CandidateResponseDto>> searchByName(@RequestParam String name){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.searchByName(name));
    }

    @GetMapping("/search/skills")
    public ResponseEntity<List<CandidateResponseDto>> searchBySkills(@RequestParam List<String> skills){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.searchBySkills(skills));
    }

    @PostMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto>addSkillToCandidate(@PathVariable Long candidateId,
                                                                   @PathVariable Long skillId){
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.addSkillToCandidate(candidateId, skillId));
    }

    @DeleteMapping("/{candidateId}/skills/{skillId}")
    public ResponseEntity<CandidateResponseDto> removeSkillFromCandidate(@PathVariable Long candidateId,
                                                                         @PathVariable Long skillId) {
        return ResponseEntity.ok(candidateService.removeSkillFromCandidate(candidateId, skillId));
    }
}



