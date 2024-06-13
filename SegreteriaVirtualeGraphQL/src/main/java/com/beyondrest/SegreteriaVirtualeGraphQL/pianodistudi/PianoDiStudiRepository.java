package com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi;

import com.beyondrest.SegreteriaVirtualeGraphQL.curriculum.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PianoDiStudiRepository extends JpaRepository<PianoDiStudi, Long> {
    @Query("SELECT p FROM PianoDiStudi p JOIN FETCH p.curriculum c WHERE c in :curricula")
    List<PianoDiStudi> findAllByCurriculum(@Param("curricula") List<Curriculum> curricula);
}
