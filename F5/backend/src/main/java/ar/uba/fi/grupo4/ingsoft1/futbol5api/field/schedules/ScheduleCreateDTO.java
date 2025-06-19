package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Function;

public record ScheduleCreateDTO (
        @NotNull String fieldName,
        @NotNull DayOfWeek dayOfWeek,
        @NotNull LocalTime openingTime,
        @NotNull LocalTime closingTime,
        @NotNull int slotDurationMinutes
) {
    public Schedule asSchedule(Function<String, Optional<Field>> getField) {
        var field = fieldName == null
                ? null
                : getField.apply(fieldName).orElseThrow(() -> new IllegalArgumentException("field"));
        return new Schedule(field, dayOfWeek, openingTime, closingTime, slotDurationMinutes);
    }
}
