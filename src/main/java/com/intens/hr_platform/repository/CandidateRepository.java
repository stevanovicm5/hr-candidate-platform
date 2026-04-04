package com.intens.hr_platform.repository;

import com.intens.hr_platform.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByFullNameContainingIgnoreCase(String fullName);

    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE s.name IN :skillNames")
    List<Candidate> findBySkillNames(String skillName);
}
