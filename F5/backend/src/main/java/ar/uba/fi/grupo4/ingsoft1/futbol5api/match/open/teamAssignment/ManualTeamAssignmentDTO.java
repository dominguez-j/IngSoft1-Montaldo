package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment;

import java.util.List;

public record ManualTeamAssignmentDTO(
        List<PlayerTeamAssignmentDTO> assignments
) { }
