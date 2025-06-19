package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.BusinessRuleException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.ManualTeamAssignmentDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.TeamAssignmentService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy.TeamAssignmentStrategyEnum;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open-matches")
public class OpenMatchRestController {

    private final OpenMatchService openMatchService;
    private final TeamAssignmentService teamAssignmentService;

    @Autowired
    public OpenMatchRestController(OpenMatchService openMatchService,
                                   TeamAssignmentService teamAssignmentService) {
        this.openMatchService = openMatchService;
        this.teamAssignmentService = teamAssignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Open Match")
    public ResponseEntity<OpenMatchDTO> createOpenMatch(@Valid @RequestBody OpenMatchCreateDTO dto) {
        OpenMatchDTO result = openMatchService.createOpenMatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List open matches (paginated)")
    public Page<OpenMatchDTO> getOpenMatches(
            OpenMatchFilterDTO dto,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable
    ) {
        return openMatchService.getOpenMatches(dto, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get open match by ID")
    public ResponseEntity<OpenMatchDTO> getOpenMatchById(@PathVariable Long id) throws ItemNotFoundException {
        return ResponseEntity.ok(openMatchService.getOpenMatchById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an open match")
    public ResponseEntity<Void> deleteOpenMatch(@PathVariable Long id) throws ItemNotFoundException, PermissionDeniedException {
        openMatchService.deleteOpenMatch(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Confirm an open match (only owner)")
    public ResponseEntity<Void> confirmOpenMatch(@PathVariable Long id) throws ItemNotFoundException, PermissionDeniedException {
        openMatchService.confirmOpenMatch(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blocked-slot/{blockedSlotId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get open match by blocked slot ID")
    public ResponseEntity<OpenMatchDTO> getByBlockedSlot(@PathVariable Long blockedSlotId) throws ItemNotFoundException {
        return ResponseEntity.ok(openMatchService.getByBlockedSlotId(blockedSlotId));
    }

    @PutMapping("/{id}/join")
    @Operation(summary = "Join an open match as a player")
    public ResponseEntity<Void> joinOpenMatch(@PathVariable Long id) throws ItemNotFoundException, BusinessRuleException {
        openMatchService.joinOpenMatch(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Leave an open match (if not confirmed)")
    public ResponseEntity<Void> leaveOpenMatch(@PathVariable Long id) throws ItemNotFoundException, PermissionDeniedException {
        openMatchService.leaveOpenMatch(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/participations")
    @Operation(summary = "Get open matches the current user is participating in, filtered by past or future")
    public ResponseEntity<Page<OpenMatchDTO>> getMyParticipations(
            @RequestParam(required = false, defaultValue = "true") Boolean isCurrent,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "blockedSlot.date") Pageable pageable
    ) {
        return ResponseEntity.ok(openMatchService.getMyParticipations(isCurrent, pageable));
    }

    @PostMapping("/{matchId}/organize-teams")
    @Operation(summary = "Sort teams according to a selected parameter")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> organizeTeamsAutomatically(
            @PathVariable Long matchId,
            @RequestParam TeamAssignmentStrategyEnum strategy
    ) throws ItemNotFoundException, BusinessRuleException, PermissionDeniedException {
        teamAssignmentService.organizeAutomatically(matchId, strategy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{matchId}/organize-teams/manual")
    @Operation(summary = "Organize teams manually")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> organizeTeamsManually (
            @PathVariable Long matchId,
            @RequestBody ManualTeamAssignmentDTO dto
    ) throws ItemNotFoundException, BusinessRuleException, PermissionDeniedException {
        teamAssignmentService.assignManually(matchId, dto);
        return ResponseEntity.ok().build();
    }
}
