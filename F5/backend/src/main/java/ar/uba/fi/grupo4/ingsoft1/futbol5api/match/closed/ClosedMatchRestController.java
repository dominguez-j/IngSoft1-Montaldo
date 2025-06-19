package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/closed-matches")
public class ClosedMatchRestController {
    private final ClosedMatchService closedMatchService;

    public ClosedMatchRestController(ClosedMatchService closedMatchService) {
        this.closedMatchService = closedMatchService;
    }

    @PostMapping
    public ResponseEntity<ClosedMatchDTO> createClosedMatch(
            @Valid @RequestBody ClosedMatchCreateDTO dto
    ) throws ItemNotFoundException {
        ClosedMatchDTO created = closedMatchService.createClosedMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
