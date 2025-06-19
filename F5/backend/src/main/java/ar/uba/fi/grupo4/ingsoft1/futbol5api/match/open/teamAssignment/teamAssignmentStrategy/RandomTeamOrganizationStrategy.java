package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("RANDOM")
public class RandomTeamOrganizationStrategy implements TeamAssignmentStrategy {
    @Override
    public void organize(List<User> players) {
        Collections.shuffle(players);
    }
}