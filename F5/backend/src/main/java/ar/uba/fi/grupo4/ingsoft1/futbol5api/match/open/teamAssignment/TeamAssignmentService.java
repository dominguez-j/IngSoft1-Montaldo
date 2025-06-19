package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.BusinessRuleException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatchRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy.TeamAssignmentStrategy;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy.TeamAssignmentStrategyEnum;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy.TeamAssignmentStrategyFactory;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamAssignmentService {
    private OpenMatchRepository openMatchRepository;
    private TeamAssignmentRepository assignmentRepository;
    private TeamAssignmentStrategyFactory teamAssignmentStrategyFactory;
    private AuthenticatedUserProvider authenticatedUserProvider;
    private UserRepository userRepository;

    @Autowired
    public TeamAssignmentService(
            OpenMatchRepository openMatchRepository,
            TeamAssignmentRepository assignmentRepository,
            TeamAssignmentStrategyFactory teamAssignmentStrategyFactory,
            AuthenticatedUserProvider authenticatedUserProvider,
            UserRepository userRepository
    ) {
        this.openMatchRepository = openMatchRepository;
        this.assignmentRepository = assignmentRepository;
        this.teamAssignmentStrategyFactory = teamAssignmentStrategyFactory;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public void organizeAutomatically(Long matchId, TeamAssignmentStrategyEnum strategyEnum) throws ItemNotFoundException, BusinessRuleException, PermissionDeniedException {
        OpenMatch match = openMatchRepository.findById(matchId)
                .orElseThrow(() -> new ItemNotFoundException("Match not found"));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!match.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new PermissionDeniedException("Only the match owner can organize this match.");
        }

        if (!Boolean.TRUE.equals(match.getConfirmed())) {
            throw new BusinessRuleException("Match must be confirmed before organizing teams");
        }

        if (!assignmentRepository.findByMatch(match).isEmpty()) {
            throw new BusinessRuleException("Teams are already organized");
        }

        List<User> players = new ArrayList<>(match.getPlayers());

        if (players.size() % 2 != 0) {
            throw new BusinessRuleException("Players count must be even to form balanced teams");
        }

        TeamAssignmentStrategy strategy = teamAssignmentStrategyFactory.getStrategy(strategyEnum);
        strategy.organize(players);

        List<User> teamA = new ArrayList<>();
        List<User> teamB = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (i % 2 == 0) teamA.add(players.get(i));
            else teamB.add(players.get(i));
        }

        for (User user : teamA) {
            TeamAssignment assignment = new TeamAssignment(match, user, TeamSide.A);
            match.addAssignment((assignment));
            assignmentRepository.save(assignment);
        }

        for (User user : teamB) {
            TeamAssignment assignment = new TeamAssignment(match, user, TeamSide.B);
            match.addAssignment((assignment));
            assignmentRepository.save(assignment);
        }
    }

    @Transactional
    public void assignManually(Long matchId, ManualTeamAssignmentDTO dto) throws ItemNotFoundException, BusinessRuleException {
        OpenMatch match = openMatchRepository.findById(matchId)
                .orElseThrow(() -> new ItemNotFoundException("Match not found"));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!match.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new PermissionDeniedException("Only the match owner can organize this match.");
        }

        if (!Boolean.TRUE.equals(match.getConfirmed())) {
            throw new BusinessRuleException("Match must be confirmed before assigning teams");
        }

        if (!assignmentRepository.findByMatch(match).isEmpty()) {
            throw new BusinessRuleException("Teams have already been assigned");
        }

        List<User> inscriptos = match.getPlayers();
        Map<String, TeamSide> assignmentMap = new HashMap<>();

        for (PlayerTeamAssignmentDTO playerAssignment : dto.assignments()) {
            String email = playerAssignment.email();

            if (assignmentMap.containsKey(email)) {
                throw new BusinessRuleException("User " + email + " is assigned to multiple teams");
            }

            assignmentMap.put(email, playerAssignment.team());
        }

        Set<String> inscriptosEmails = inscriptos.stream().map(u -> u.getEmail().toLowerCase()).collect(Collectors.toSet());

        if (!assignmentMap.keySet().equals(inscriptosEmails)) {
            throw new BusinessRuleException("Not all registered players are assigned to a team or extra players are included");
        }

        long countTeamA = assignmentMap.values().stream().filter(t -> t == TeamSide.A).count();
        long countTeamB = assignmentMap.values().stream().filter(t -> t == TeamSide.B).count();

        if (countTeamA != countTeamB) {
            throw new BusinessRuleException("Teams must be balanced: both teams must have the same number of players");
        }

        for (Map.Entry<String, TeamSide> entry : assignmentMap.entrySet()) {
            User user = userRepository.findByEmail(entry.getKey())
                    .orElseThrow(() -> new ItemNotFoundException("User not found: " + entry.getKey()));

            TeamAssignment assignment = new TeamAssignment(match, user, entry.getValue());
            assignmentRepository.save(assignment);
        }
    }
}
