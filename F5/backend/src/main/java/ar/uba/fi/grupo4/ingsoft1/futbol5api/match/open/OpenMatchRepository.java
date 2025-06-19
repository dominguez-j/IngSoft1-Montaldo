package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed.ClosedMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface OpenMatchRepository extends JpaRepository<OpenMatch, Long>, JpaSpecificationExecutor<OpenMatch> {
    Optional<OpenMatch> findByBlockedSlot_Id(Long blockedSlotId);
    List<OpenMatch> findByFieldIn(List<Field> fields);
    Page<OpenMatch> findByOwner(User owner, Pageable pageable);
}
