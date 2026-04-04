package com.intens.hr_platform.controller;

import com.intens.hr_platform.dto.skill.SkillRequestDto;
import com.intens.hr_platform.dto.skill.SkillResponseDto;
import com.intens.hr_platform.service.SkillService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/skills")
@AllArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponseDto> addSkill(@Valid @RequestBody SkillRequestDto request){
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.addSkill(request));
    }

    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getAllSkills(){
        return ResponseEntity.status(HttpStatus.OK).body(skillService.getAllSkills());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id){
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SkillResponseDto> updateSkill(@PathVariable Long id,
                                                   @Valid @RequestBody SkillRequestDto request){
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }
}
