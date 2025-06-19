package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.Schedule;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public record BlockedSlotCreateDTO(
        String fieldName,
        @NotNull @FutureOrPresent LocalDate date,
        @NotNull @Min(0) int slotNumber,
        String reason
) {
    public BlockedSlot asBlockedSlot(
            Function<String, Optional<Field>> getField,
            BiFunction<Field, DayOfWeek, Optional<Schedule>> getSchedule,
            Supplier<User> getOwner
    ) {
        var field = fieldName == null ?
                null :
                getField.apply(fieldName).orElseThrow(() -> new IllegalArgumentException(
                        "Invalid field name: " + fieldName
                ));
        Optional<Schedule> optionalSchedule = field == null ?
                Optional.empty() :
                getSchedule.apply(field, date.getDayOfWeek());
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException("" +
                    "Invalid field name (" + fieldName +") or date (" + date + ")"
            );
        }

        Schedule schedule = optionalSchedule.get();
        var schedOpeningTime = schedule.getOpeningTime();
        var slotDuration = schedule.getSlotDurationMinutes();
        var numberOfSlots = schedule.getNumberOfSlots();
        if (slotNumber >= numberOfSlots) {
            throw new IllegalArgumentException(
                    "Slot number " + slotNumber + " is equal or greater than number of slots (" + numberOfSlots + ")"
            );
        }
        LocalTime startTime = schedOpeningTime.plusMinutes(slotNumber * slotDuration);
        LocalTime endTime = startTime.plusMinutes(slotDuration);

        User owner = getOwner.get();

        return new BlockedSlot(
                field,
                owner,
                date,
                startTime,
                endTime,
                slotNumber,
                reason
        );
    };
}
