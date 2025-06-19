package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserRepository userRepository, AuthenticatedUserProvider authenticatedUserProvider){
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void createTeam(TeamCreateDTO dto) throws IllegalArgumentException {
        String teamName = dto.teamName();
        if (teamRepository.existsByTeamName(dto.teamName())) {
            throw new IllegalArgumentException("Team name already registered");
        }
        teamRepository.save(dto.asTeam(authenticatedUserProvider::getAuthenticatedUser));
    }

    public void deleteTeam(String teamName) {
        Team team = teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found: " + teamName));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        if (!team.getTeamOwner().getEmail().equals(currentUser.getEmail())) {
            throw new SecurityException("You are not the captain of this team");
        }

        teamRepository.deleteById(team.getId());
    }

    public void updateTeam(String teamName, TeamCreateDTO dto) {
        Team team = teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found: " + teamName));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        User teamOwner = team.getTeamOwner();

        if (!currentUser.getEmail().equals(teamOwner.getEmail())) {
            throw new SecurityException("You are not the captain of this team");
        }

        teamRepository.save(dto.asTeam(team.getId(), authenticatedUserProvider::getAuthenticatedUser));
    }

    public Page<TeamDTO> getTeams(boolean owned, boolean joined, boolean includeMembers, Pageable pageable) {
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        Page<Team> teams;

        if (owned && joined) {
            teams = teamRepository.findByTeamOwnerOrMembersContaining(currentUser, currentUser, pageable);
        } else if (owned) {
            teams = teamRepository.findByTeamOwner(currentUser, pageable);
        } else if (joined) {
            teams = teamRepository.findByMembersContainingAndTeamOwnerNot(currentUser, currentUser, pageable);
        } else {
            teams = Page.empty(pageable);
        }

        return teams.map(team -> new TeamDTO(team, includeMembers));
    }

    public void addMember(String teamName, String memberEmail) {
        Team team = teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found: " + teamName));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!team.getTeamOwner().getEmail().equals(currentUser.getEmail())) {
            throw new SecurityException("You are not the captain of this team");
        }

        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + memberEmail));

        if (team.getMembers().contains(member)) {
            throw new IllegalStateException("User is already a member of this team.");
        }

        List<Team> joinedTeams = teamRepository.findByMembersContaining(member);
        boolean alreadyInAnyTeam = joinedTeams.stream().anyMatch(t -> !t.equals(team));

        if (alreadyInAnyTeam) {
            throw new IllegalStateException("User is already a member of another team.");
        }

        team.addMember(member);
        teamRepository.save(team);
    }

    public void removeMember(String teamName, String memberEmail) {
        Team team = teamRepository.findByTeamName(teamName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found: " + teamName));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!team.getTeamOwner().getEmail().equals(currentUser.getEmail())) {
            throw new SecurityException("You are not the captain of this team");
        }

        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + memberEmail));

        team.removeMember(member);
        teamRepository.save(team);
    }

}