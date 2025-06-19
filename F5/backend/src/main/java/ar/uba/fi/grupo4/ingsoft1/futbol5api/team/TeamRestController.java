package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@Tag(name = "Teams")
public class TeamRestController {

    private final TeamService teamService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public TeamRestController(TeamService teamService, AuthenticatedUserProvider authenticatedUserProvider){
        this.teamService = teamService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Create a new team")
    public ResponseEntity<Void> createTeam(
            @Valid @RequestBody TeamCreateDTO data
    ){
        teamService.createTeam(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{teamName}")
    @Operation(summary = "Deletes a team if you are the team owner.")
    public ResponseEntity<Object> deleteTeam(
            @PathVariable String teamName) {
        teamService.deleteTeam(teamName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{teamName}")
    @Operation(summary = "Update team info (only by the owner)")
    public ResponseEntity<Void> updateTeam(
            @PathVariable String teamName,
            @Valid @RequestBody TeamCreateDTO dto
    ) {
        teamService.updateTeam(teamName, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Returns a page with teams")
    public Page<TeamDTO> getTeams(
            @RequestParam(defaultValue = "true") boolean owned,
            @RequestParam(defaultValue = "false") boolean joined,
            @RequestParam(defaultValue = "false") boolean includeMembers,
            @PageableDefault(page = 0, size = 10)
            @Valid @ParameterObject Pageable pageable
    ) {
        return teamService.getTeams(owned, joined, includeMembers, pageable);
    }

    @PostMapping("/{teamName}/members")
    @Operation(summary = "Add a member to the team (only owner allowed)")
    public ResponseEntity<Void> addMember(
            @PathVariable String teamName,
            @Valid @RequestBody String userMail) throws ItemNotFoundException {
        teamService.addMember(teamName, userMail);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamName}/members/{userMail}")
    @Operation(summary = "Remove a member from the team (only owner allowed)")
    public ResponseEntity<Void> removeMember(
            @PathVariable String teamName,
            @PathVariable String userMail) throws ItemNotFoundException {
        teamService.removeMember(teamName, userMail);
        return ResponseEntity.noContent().build();
    }

}



