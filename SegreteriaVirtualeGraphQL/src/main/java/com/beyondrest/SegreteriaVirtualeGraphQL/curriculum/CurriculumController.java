package com.beyondrest.SegreteriaVirtualeGraphQL.curriculum;

import com.beyondrest.SegreteriaVirtualeGraphQL.corso.CorsoRepository;
import com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi.PianoDiStudi;
import com.beyondrest.SegreteriaVirtualeGraphQL.pianodistudi.PianoDiStudiRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import com.beyondrest.SegreteriaVirtualeGraphQL.corso.Corso;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CurriculumController {
    private final CurriculumRepository curriculumRepository;
    private final CorsoRepository corsoRepository;

    private final PianoDiStudiRepository pianoDiStudiRepository;

    CurriculumController(CurriculumRepository curriculumRepository, CorsoRepository corsoRepository, PianoDiStudiRepository pianoDiStudiRepository) {
        this.curriculumRepository = curriculumRepository;
        this.corsoRepository = corsoRepository;
        this.pianoDiStudiRepository = pianoDiStudiRepository;
    }
    @QueryMapping
    public Curriculum curriculum(@Argument Long corsoId, @Argument Long id) {
        return corsoRepository.findById(corsoId).stream().flatMap(corso -> corso.getCurricula().stream())
                .filter(curriculum -> curriculum.getId().equals(id)).findFirst().orElse(null);
    }

    @QueryMapping
    public List<Curriculum> curricula(@Argument Long corsoId) {
        return corsoRepository.findById(corsoId).map(Corso::getCurricula).orElse(null);
    }

    @QueryMapping
    public PianoDiStudi pianoDiStudi(@Argument Long corsoId, @Argument Long id, @Argument String anno) {
        return corsoRepository.findById(corsoId).orElseThrow().getCurricula().stream()
                .filter(curriculum -> curriculum.getId().equals(id))
                .findFirst()
                .orElseThrow().getPianiDiStudi().stream()
                .filter(pianoDiStudi -> pianoDiStudi.getAnno().equals(anno))
                .findFirst()
                .orElseThrow();
    }

    @BatchMapping(field = "pianiDiStudi")
    public Map<Curriculum, List<PianoDiStudi>> pianiDiStudi(List<Curriculum> curricula) {
        var pianiDiStudi = pianoDiStudiRepository.findAllByCurriculum(curricula);
        return curricula.stream().collect(Collectors.toMap(curriculum -> curriculum, curriculum -> pianiDiStudi.stream()
                .filter(pianoDiStudi -> pianoDiStudi.getCurriculum().equals(curriculum))
                .collect(Collectors.toList())));
    }
}
