package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("BY_AGE")
public class ByAgeTeamAssignmentStrategy implements TeamAssignmentStrategy {
    @Override
    public void organize(List<User> players) {
        players.sort(Comparator.comparing(User::getAge));
    }
}