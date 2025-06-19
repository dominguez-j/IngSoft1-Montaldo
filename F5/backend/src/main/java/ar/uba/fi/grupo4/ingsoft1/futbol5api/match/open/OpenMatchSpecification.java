package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OpenMatchSpecification {

    public static Specification<OpenMatch> isOwnedByUser(Boolean owned, User user) {
        return owned == null || !owned ? null
                : (root, query, cb) -> cb.equal(root.get("owner"), user);
    }

    public static Specification<OpenMatch> hasFieldName(String fieldName) {
        return fieldName == null || fieldName.isEmpty() ? null
                : (root, query, cb) -> cb.equal(root.get("field").get("name"), fieldName);
    }

    public static Specification<OpenMatch> hasDate(LocalDate date) {
        return date == null ? null
                : (root, query, cb) -> cb.equal(root.get("blockedSlot").get("date"), date);
    }

    public static Specification<OpenMatch> isNotFull() {
        return (root, query, cb) -> cb.lt(cb.size(root.get("players")), root.get("maxPlayers"));
    }

    public static Specification<OpenMatch> isInPresentOrFutureDate() {
        return (root, query, cb) -> {
            Join<OpenMatch, BlockedSlot> blockedSlot = root.join("blockedSlot");
            return cb.greaterThanOrEqualTo(blockedSlot.get("date"), LocalDate.now());
        };
    }

    public static Specification<OpenMatch> getSpecification(OpenMatchFilterDTO filter, User user) {
        return Specification.where(isOwnedByUser(filter.owned(), user))
                .and(hasFieldName(filter.fieldName()))
                .and(hasDate(filter.date()))
                .and(isInPresentOrFutureDate())
                .and(isNotFull());
    }
}
