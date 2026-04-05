package com.intens.hr_platform.controller;

import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/skills")
@AllArgsConstructor
@Tag(name = "Skills", description = "Skill management API")
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "Add new skill")
    @PostMapping
    public ResponseEntity<SkillResponseDto> addSkill(@Valid @RequestBody SkillRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.addSkill(request));
    }

    @Operation(summary = "Get all skills")
    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getAllSkills(){
        return ResponseEntity.status(HttpStatus.OK).body(skillService.getAllSkills());
    }

    @Operation(summary = "Delete skill by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id){
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update skill by ID")
    @PatchMapping("/{id}")
    public ResponseEntity<SkillResponseDto> updateSkill(@PathVariable Long id,
                                                   @Valid @RequestBody SkillRequestDto request){
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }
}
