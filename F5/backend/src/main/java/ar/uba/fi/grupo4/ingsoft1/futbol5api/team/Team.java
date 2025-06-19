package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "team")
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String teamName;

    @Column(unique = false, nullable = false)
    private String primaryColor;

    @Column(unique = false, nullable = true)
    private String subColor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "captain_id")
    @JsonIgnore
    private User teamOwner;

    @Column(unique = false, nullable = true)
    private Ranking ranking;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    public Team(){}

    public Team(
            Long id,
            String teamName,
            String primaryColor,
            String subColor,
            User teamOwner,
            Ranking ranking) {
        this.id = id;
        this.teamName = teamName;
        this.primaryColor = primaryColor;
        this.subColor = subColor;
        this.teamOwner = teamOwner;
        this.ranking = ranking;
    }

    public List<User> getMembers(){
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setColor(String primaryColor, String subColor) {
        this.primaryColor = primaryColor;
        this.subColor = subColor;
    }


    public Ranking getRanking() {
        return ranking;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSubColor() {
        return subColor;
    }

    public User getTeamOwner() {
        return teamOwner;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setTeamOwner(User teamOwner) {
        this.teamOwner = teamOwner;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    public void addMember(User member){ members.add(member); }

    public void removeMember(User member){members.remove(member);}

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSubColor(String subColor) {
        this.subColor = subColor;
    }
}