package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;

import java.util.List;

public interface TeamAssignmentStrategy {
    void organize(List<User> players);
}