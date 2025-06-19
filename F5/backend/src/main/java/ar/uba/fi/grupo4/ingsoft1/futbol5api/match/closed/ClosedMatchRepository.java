package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.Team;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ClosedMatchRepository extends JpaRepository<ClosedMatch, Long>{
    Page<ClosedMatch> findByOwner(User owner, Pageable pageable);
    List<ClosedMatch> findByField(Field field);
    List<ClosedMatch> findByFieldIn(List<Field> fields);

}
