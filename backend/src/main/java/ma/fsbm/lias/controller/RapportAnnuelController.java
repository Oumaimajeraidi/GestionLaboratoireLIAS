package ma.fsbm.lias.controller;

import ma.fsbm.lias.dto.rapport.RapportAnnuelResponse;
import ma.fsbm.lias.service.RapportAnnuelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rapport-annuel")
public class RapportAnnuelController {

    private final RapportAnnuelService rapportAnnuelService;

    public RapportAnnuelController(RapportAnnuelService rapportAnnuelService) {
        this.rapportAnnuelService = rapportAnnuelService;
    }

    @GetMapping
    public RapportAnnuelResponse generer(@RequestParam(defaultValue = "2025") int annee) {
        return rapportAnnuelService.generer(annee);
    }
}
