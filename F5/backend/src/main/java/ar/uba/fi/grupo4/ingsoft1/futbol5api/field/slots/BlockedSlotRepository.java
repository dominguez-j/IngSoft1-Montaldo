package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long>, JpaSpecificationExecutor<BlockedSlot> {
    List<BlockedSlot> findByFieldAndDate(Field field, LocalDate date);

    Page<BlockedSlot> findByFieldAndDateBetween(Field field, LocalDate dateAfter, LocalDate dateBefore, Pageable pageable);

    Page<BlockedSlot> findByBlockOwner(User blockOwner, Pageable pageable);

    boolean existsByField_NameAndSlotNumberAndDate(String fieldName, int slotNumber, LocalDate date);
}
