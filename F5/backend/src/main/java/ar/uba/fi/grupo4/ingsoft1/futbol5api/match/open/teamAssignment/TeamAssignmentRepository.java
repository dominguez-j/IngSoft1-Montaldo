package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamAssignmentRepository extends JpaRepository<TeamAssignment, Long> {
    List<TeamAssignment> findByMatch(OpenMatch match);
    List<TeamAssignment> findByMatchAndTeam(OpenMatch match, TeamSide team);
    Optional<TeamAssignment> findByMatchAndPlayer(OpenMatch match, User player);
}
