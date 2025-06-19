package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ScheduleDTO (
        String fieldName,
        DayOfWeek dayOfWeek,
        LocalTime openingTime,
        LocalTime closingTime,
        int slotDurationMinutes
) {
    public ScheduleDTO(Schedule schedule) {
        this(
                schedule.getField().getName(),
                schedule.getDayOfWeek(),
                schedule.getOpeningTime(),
                schedule.getClosingTime(),
                schedule.getSlotDurationMinutes()
        );
    }
}