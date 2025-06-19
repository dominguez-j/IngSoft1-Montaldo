package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class BlockedSlotSpecification {

    public static Specification<BlockedSlot> belongsToUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("blockOwner"), user);
    }

    public static Specification<BlockedSlot> isPast() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return (root, query, cb) -> cb.or(
                cb.lessThan(root.get("date"), today),
                cb.and(
                        cb.equal(root.get("date"), today),
                        cb.lessThan(root.get("startTime"), now)
                )
        );
    }

    public static Specification<BlockedSlot> isCurrent() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return (root, query, cb) -> cb.or(
                cb.greaterThan(root.get("date"), today),
                cb.and(
                        cb.equal(root.get("date"), today),
                        cb.greaterThanOrEqualTo(root.get("startTime"), now)
                )
        );
    }

    public static Specification<BlockedSlot> getSpecification(Boolean isCurrent, User user) {
        Specification<BlockedSlot> spec = belongsToUser(user);

        if (Boolean.FALSE.equals(isCurrent))
            return spec.and(isPast());

        return spec.and(isCurrent());
    }
}