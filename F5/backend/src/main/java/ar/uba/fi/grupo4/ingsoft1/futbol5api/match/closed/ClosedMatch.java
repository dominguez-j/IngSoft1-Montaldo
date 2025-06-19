package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.Team;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.persistence.*;

@Entity(name = "closed_match")
@Table(name = "closed_match")
public class ClosedMatch {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User owner;

    @ManyToOne
    @JoinColumn
    private Team teamA;

    @ManyToOne
    @JoinColumn
    private Team teamB;

    @ManyToOne
    @JoinColumn
    private Field field;

    @Column(nullable = false)
    private Boolean confirmed;

    @OneToOne
    @JoinColumn(
            name = "blocked_slot_id",
            foreignKey = @ForeignKey(name = "fkbymgl3191uht7w9d0w4l2rmh8")
    )
    private BlockedSlot blockedSlot;

    public ClosedMatch() {}

    public ClosedMatch(
            User owner,
            Field field,
            Team TeamA,
            Team TeamB,
            Boolean confirmed,
            BlockedSlot blockedSlot
    ){
        this.owner=owner;
        this.field=field;
        this.teamA=TeamA;
        this.teamB=TeamB;
        this.confirmed=confirmed;
        this.blockedSlot=blockedSlot;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public Team getTeamA() {return teamA;}
    public Team getTeamB() {return teamB;}
    public Boolean getConfirmed() {return confirmed;}

    public Field getField() {
        return field;
    }

    public BlockedSlot getBlockedSlot() {
        return blockedSlot;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setTeamA(Team teamA) {this.teamA = teamA;}
    public void setTeamB(Team teamB) {this.teamB = teamB;}

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setBlockedSlot(BlockedSlot blockedSlot) {
        this.blockedSlot = blockedSlot;
    }



}
