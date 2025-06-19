package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field> {
    boolean existsByName(String name);

    Optional<Field> findByName(String name);
    Optional<Field> findByNameAndEnabled(String name, Boolean enabled);
    Boolean existsByNameAndEnabled(String name, Boolean active);

    Page<Field> findByOwner(User owner, Pageable pageable);

    void deleteByName(String name);
}
