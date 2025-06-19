package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserDTO;

import java.util.List;

public record TeamAssignmentsDTO(
        List<UserDTO> teamA,
        List<UserDTO> teamB
) {
    public TeamAssignmentsDTO(OpenMatch match) {
        this(
                match.getTeamAssignments().stream()
                        .filter(a -> a.getTeam() == TeamSide.A)
                        .map(a -> new UserDTO(a.getPlayer()))
                        .toList(),
                match.getTeamAssignments().stream()
                        .filter(a -> a.getTeam() == TeamSide.B)
                        .map(a -> new UserDTO(a.getPlayer()))
                        .toList()
        );
    }
}