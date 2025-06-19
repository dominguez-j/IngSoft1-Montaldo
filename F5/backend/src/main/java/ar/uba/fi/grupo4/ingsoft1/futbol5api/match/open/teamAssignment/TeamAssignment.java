package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserDTO;
import jakarta.persistence.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;

@Entity
@Table(name = "match_team_assignments")
public class TeamAssignment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private OpenMatch match;

    @ManyToOne
    private User player;

    @Enumerated(EnumType.STRING)
    private TeamSide team;

    public TeamAssignment(){};

    public TeamAssignment(OpenMatch match,
                          User player,
                          TeamSide team) {
        this.match = match;
        this.player = player;
        this.team = team;
    }

    public void setMatch(OpenMatch match) {
        this.match = match;
    }

    public TeamSide getTeam() {
        return this.team;
    }

    public User getPlayer() {
        return this.player;
    }
}