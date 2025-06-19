package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.free;

import java.time.LocalDate;
import java.time.LocalTime;

public record FreeSlotDTO(
        String fieldName,
        LocalDate date,
        int slotNumber,
        LocalTime startTime,
        LocalTime endTime
) {
}
