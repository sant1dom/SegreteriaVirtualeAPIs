package com.beyondrest.SegreteriaVirtualeGraphQL.curriculum;

import com.beyondrest.SegreteriaVirtualeGraphQL.corso.Corso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findAllByCorsoIn(List<Corso> corsi);
}
