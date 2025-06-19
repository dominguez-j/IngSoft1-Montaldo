package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record OpenMatchCreateDTO(
        @NotNull @Min(0) Integer minPlayers,
        @NotNull @Min(0) Integer maxPlayers,
        @NotBlank String fieldName,
        @NotNull Long blockedSlotId
) {
    public OpenMatch asOpenMatch(
            Function<String, Optional<Field>> getField,
            Function<Long, Optional<BlockedSlot>> getReserve,
            Supplier<User> getOwner
    ) {
        Field field = getField.apply(fieldName)
                .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldName));

        BlockedSlot blockedSlot = getReserve.apply(blockedSlotId)
                .orElseThrow(() -> new IllegalArgumentException("BlockedSlot not found: " + blockedSlotId));

        if (!blockedSlot.getField().getId().equals(field.getId())) {
            throw new IllegalArgumentException("BlockedSlot does not belong to field: " + fieldName);
        }

        return new OpenMatch(
                getOwner.get(),
                minPlayers,
                maxPlayers,
                true,
                field,
                blockedSlot
        );
    }
}
