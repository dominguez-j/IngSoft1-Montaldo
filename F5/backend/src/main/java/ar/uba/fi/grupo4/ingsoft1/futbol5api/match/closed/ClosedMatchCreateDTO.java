package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.Team;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record ClosedMatchCreateDTO(
        @NotBlank String fieldName,
        @NotNull String teamAName,
        @NotNull String teamBName,
        @NotNull Long blockedSlotId
) {
    public ClosedMatch asClosedMatch(
            Function<String, Optional<Field>> getField,
            Function<Long, Optional<BlockedSlot>> getReserve,
            Supplier<User> getOwner,
            Function<String, Optional<Team>> getTeam


    ) throws ItemNotFoundException  {
        Field field = getField.apply(fieldName)
                .orElseThrow(() -> new ItemNotFoundException("Field not found: " + fieldName));
        Team teamA = getTeam.apply(teamAName)
                .orElseThrow(() -> new ItemNotFoundException("Team A not found: " + teamAName));
        Team teamB = getTeam.apply(teamBName)
                .orElseThrow(() -> new ItemNotFoundException("Team B not found: " + teamBName));
        BlockedSlot reservedSlot = getReserve.apply(blockedSlotId)
                .orElseThrow(() -> new ItemNotFoundException("BlockedSlot not found: " + blockedSlotId));
        User owner = getOwner.get();

        return new ClosedMatch(owner,field,teamA,teamB,true,reservedSlot);
    }

}
