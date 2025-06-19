package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.TeamAssignment;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "open_match")
@Table(name = "open_matches")
public class OpenMatch {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "match_players",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> players;

    @Column(unique = false, nullable = false)
    private Integer minPlayers;

    @Column(unique = false, nullable = false)
    private Integer maxPlayers;

    @Column(unique = false, nullable = false)
    private Boolean confirmed;

    @ManyToOne
    @JoinColumn
    private Field field;

    @OneToOne
    @JoinColumn(
            name = "blocked_slot_id",
            foreignKey = @ForeignKey(name = "fk_open_match_blocked_slot")
    )
    private BlockedSlot blockedSlot;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamAssignment> teamAssignments = new HashSet<>();

    public OpenMatch() {}

    public OpenMatch(
            User owner,
            Integer minPlayers,
            Integer maxPlayers,
            Boolean confirmed,
            Field field,
            BlockedSlot blockedSlot
    ) {
        this.owner = owner;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.confirmed = confirmed;
        this.field = field;
        this.blockedSlot = blockedSlot;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getPlayers() {
        return players;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public Field getField() {
        return field;
    }

    public BlockedSlot getBlockedSlot() {
        return blockedSlot;
    }

    public Set<TeamAssignment> getTeamAssignments() {
        return teamAssignments;
    }

    public void addAssignment(TeamAssignment assignment) {
        this.teamAssignments.add(assignment);
        assignment.setMatch(this);
    }

    public void removeAssignment(TeamAssignment assignment) {
        this.teamAssignments.remove(assignment);
        assignment.setMatch(null);
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public void setMinPlayers(Integer minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

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