package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.comment.Comment;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.Schedule;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity(name = "fields")
@Table(name = "fields")
public class Field {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = false, nullable = false)
    private Boolean enabled = true;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = false, nullable = false)
    private GroundType groundType;

    @Column(unique = false, nullable = false)
    private Boolean hasRoof;

    @Column(unique = false, nullable = false)
    private Boolean hasIllumination;

    @Column(unique = false, nullable = false)
    private String zone;

    @Column(unique = false, nullable = false)
    private String address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Schedule> schedulesPerDay;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BlockedSlot> blockedSlots;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;

    public Field() {}

    public Field(
            Long id,
            String name,
            boolean enabled,
            GroundType groundType,
            boolean hasRoof,
            boolean hasIllumination,
            String zone,
            String address,
            User owner) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.groundType = groundType;
        this.hasRoof = hasRoof;
        this.hasIllumination = hasIllumination;
        this.zone = zone;
        this.address = address;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public GroundType getGroundType() {
        return groundType;
    }

    public boolean getHasRoof() {
        return hasRoof;
    }

    public boolean getHasIllumination() {
        return hasIllumination;
    }

    public String getZone() {
        return zone;
    }

    public String getAddress() {
        return address;
    }

    public User getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setName(String name) {this.name = name;}

    public void setGroundType(GroundType groundType) {this.groundType = groundType;}

    public void setHasRoof(boolean hasRoof) {this.hasRoof = hasRoof;}

    public void setHasIllumination(boolean hasIllumination) {this.hasIllumination = hasIllumination;}

    public void setZone(String zone) {this.zone = zone;}

    public void setAddress(String address) {this.address = address;}

    public void setSchedulesPerDay(List<Schedule> schedulesPerDay) {
        this.schedulesPerDay = schedulesPerDay;
    }

    public List<Schedule> getSchedulesPerDay() {
        return schedulesPerDay;
    }

    public List<BlockedSlot> getBlockedSlots() {
        return blockedSlots;
    }

    public boolean isHasRoof() {
        return hasRoof;
    }

    public boolean isHasIllumination() {
        return hasIllumination;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setBlockedSlots(List<BlockedSlot> blockedSlots) {
        this.blockedSlots = blockedSlots;
    }
}
