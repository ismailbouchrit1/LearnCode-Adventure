package com.learncode.repository;

import com.learncode.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);

    // ── FIX : requête JPQL explicite compatible H2 ──────────────
    @Query("SELECT s FROM Student s ORDER BY s.score DESC")
    List<Student> findAllOrderByScoreDesc();
}
