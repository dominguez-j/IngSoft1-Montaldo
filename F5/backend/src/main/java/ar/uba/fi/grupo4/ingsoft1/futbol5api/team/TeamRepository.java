package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByTeamName(String name);

    Optional<Team> findByTeamName(String teamName);
    Page<Team> findByTeamOwner(User teamOwner, Pageable pageable);
    Page<Team> findByMembersContaining(User user, Pageable pageable);
    List<Team> findByMembersContaining(User user);
    Page<Team> findByMembersContainingAndTeamOwnerNot(User user, User notOwner, Pageable pageable);
    Page<Team> findByTeamOwnerOrMembersContaining(User owner, User member, Pageable pageable);
}