package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByField(Field field, Pageable pageable);
}
