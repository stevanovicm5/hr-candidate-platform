package com.intens.hr_platform.repository;

import com.intens.hr_platform.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByFullNameContainingIgnoreCase(String fullName);
    Optional<Candidate> findByEmailIgnoreCase(String email);

    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE LOWER(s.name) IN :skillNames")
    List<Candidate> findBySkillNames(@Param("skillNames") List<String> skillNames);

    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE s.id = :skillId")
    List<Candidate> findBySkillId(@Param("skillId") Long skillId);

    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Candidate> findBySkillsContaining(@Param("searchTerm") String searchTerm);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByContactNumber(String contactNumber);
    boolean existsByContactNumberAndIdNot(String contactNumber, Long id);
}

