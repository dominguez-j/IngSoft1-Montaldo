package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import java.time.LocalDate;
import java.time.LocalTime;

public record BlockedSlotDTO(
        Long id,
        String fieldName,
        String ownerName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String reason
) {
    public BlockedSlotDTO(BlockedSlot blockedSlot) {
        this(
                blockedSlot.getId(),
                blockedSlot.getField().getName(),
                blockedSlot.getBlockOwner().getName(),
                blockedSlot.getDate(),
                blockedSlot.getStartTime(),
                blockedSlot.getEndTime(),
                blockedSlot.getReason()
        );
    }
}
