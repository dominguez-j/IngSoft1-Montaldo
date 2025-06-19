package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed.ClosedMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "blocked_slots",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"field_id", "date", "slot_number"})
        }
)

public class BlockedSlot {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    @JsonIgnore
    private Field field;

    @ManyToOne(optional = false)
    @JoinColumn(name = "block_owner_id", nullable = false)
    @JsonIgnore
    private User blockOwner;

    // AAAA-MM-DD
    @Column(nullable = false)
    @FutureOrPresent
    private LocalDate date;

    // HH:MM
    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private int slotNumber;

    @Column(nullable = false)
    private String reason;


    @OneToOne(
            mappedBy    = "blockedSlot",
            cascade     = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private ClosedMatch closedMatch;

    @OneToOne(
            mappedBy = "blockedSlot",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private OpenMatch openMatch;

    protected BlockedSlot() {}

    public BlockedSlot(Field field, User blockOwner, LocalDate date, LocalTime startTime, LocalTime endTime, int slotNumber, String reason) {
        this.field = field;
        this.blockOwner = blockOwner;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotNumber = slotNumber;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public Field getField() {
        return field;
    }

    public User getBlockOwner() {
        return blockOwner;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getReason() {
        return reason;
    }

    public ClosedMatch getClosedMatch() {
        return closedMatch;
    }

    public void setClosedMatch(ClosedMatch closedMatch) {
        this.closedMatch = closedMatch;
    }

    public void setReason(String reason) {this.reason = reason;}
}
