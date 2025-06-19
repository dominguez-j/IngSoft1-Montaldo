package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.TeamAssignmentsDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public record OpenMatchDTO(
        Long id,
        UserDTO owner,
        Page<UserDTO> players,
        Integer minPlayers,
        Integer maxPlayers,
        String fieldName,
        BlockedSlotDTO blockedSlotDTO,
        Boolean confirmed,
        TeamAssignmentsDTO teamAssignmentsDTO
) {
    public OpenMatchDTO (OpenMatch openMatch) {
        this(
                openMatch.getId(),
                new UserDTO(openMatch.getOwner()),
                new PageImpl<>(openMatch.getPlayers().stream().map(UserDTO::new).toList()),
                openMatch.getMinPlayers(),
                openMatch.getMaxPlayers(),
                openMatch.getField().getName(),
                new BlockedSlotDTO(openMatch.getBlockedSlot()),
                openMatch.getConfirmed(),
                new TeamAssignmentsDTO(openMatch)
        );
    }
}