package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class OpenMatchPlayerSpecification {

    public static Specification<OpenMatch> userIsPlayer(User user) {
        return (root, query, cb) -> {
            Join<OpenMatch, User> players = root.join("players");
            return cb.equal(players, user);
        };
    }

    public static Specification<OpenMatch> isPast() {
        return (root, query, cb) -> {
            Join<OpenMatch, BlockedSlot> blockedSlot = root.join("blockedSlot");

            Expression<LocalDate> date = blockedSlot.get("date");
            Expression<LocalTime> time = blockedSlot.get("startTime");

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            return cb.or(
                    cb.lessThan(date, nowDate),
                    cb.and(
                            cb.equal(date, nowDate),
                            cb.lessThan(time, nowTime)
                    )
            );
        };
    }

    public static Specification<OpenMatch> isFuture() {
        return (root, query, cb) -> {
            Join<OpenMatch, BlockedSlot> blockedSlot = root.join("blockedSlot");

            Expression<LocalDate> date = blockedSlot.get("date");
            Expression<LocalTime> time = blockedSlot.get("startTime");

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            return cb.or(
                    cb.greaterThan(date, nowDate),
                    cb.and(
                            cb.equal(date, nowDate),
                            cb.greaterThanOrEqualTo(time, nowTime)
                    )
            );
        };
    }

    public static Specification<OpenMatch> getSpecification(Boolean isCurrent, User user) {
        Specification<OpenMatch> spec = userIsPlayer(user);

        if (isCurrent != null) {
            spec = spec.and(isCurrent ? isFuture() : isPast());
        }

        return spec;
    }
}