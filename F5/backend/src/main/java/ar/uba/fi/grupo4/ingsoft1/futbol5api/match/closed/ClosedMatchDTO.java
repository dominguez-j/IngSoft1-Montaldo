package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserDTO;


public record ClosedMatchDTO(
        UserDTO owner,
        TeamDTO teamA,
        TeamDTO teamB,
        String fieldName,
        BlockedSlotDTO blockedSlotDTO
) {
    public ClosedMatchDTO(ClosedMatch closedMatch) {
        this(
                new UserDTO(closedMatch.getOwner()),
                new TeamDTO(closedMatch.getTeamA(), false),
                new TeamDTO(closedMatch.getTeamB(),false ),
                closedMatch.getField().getName(),
                new BlockedSlotDTO(closedMatch.getBlockedSlot())
        );
    }
}