package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "schedules",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"field_id", "day_of_week"})
        }
)
public class Schedule {
    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    private Field field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private int slotDurationMinutes;

    protected Schedule() {}

    public Schedule(Field field, DayOfWeek dayOfWeek, LocalTime openingTime, LocalTime closingTime, int slotDurationMinutes) {
        this.field = field;
        this.dayOfWeek = dayOfWeek;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Long getId() {
        return id;
    }

    public Field getField() {
        return field;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public int getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public int getNumberOfSlots() {
        return (int) Math.floor((double) (closingTime.toSecondOfDay() - openingTime.toSecondOfDay()) / (double) (60 * slotDurationMinutes));
    }
}
