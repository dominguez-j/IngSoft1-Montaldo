package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.jpa.domain.Specification;


public class FieldSpecification {
    public static Specification<Field> isOwnedByUser(Boolean owned, User user) {
        return owned == null || !owned ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner"), user);
    }

    public static Specification<Field> matchesEnabled(Boolean enabled) {
        return enabled == null ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("enabled"), enabled);
    }

    public static Specification<Field> hasName(String fieldName) {
        return fieldName == null || fieldName.isEmpty() ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), fieldName);
    }

    public static Specification<Field> hasGroundType(GroundType groundType) {
        return groundType == null ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("groundType"), groundType);
    }

    public static Specification<Field> hasRoof(Boolean roof) {
        return roof == null ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hasRoof"), roof);
    }

    public static Specification<Field> hasIllumination(Boolean illumination) {
        return illumination == null ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("hasIllumination"), illumination);
    }

    public static Specification<Field> matchesZone(String zone) {
        return zone == null || zone.isEmpty() ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("zone"), zone);
    }

    public static Specification<Field> matchesAddress(String address) {
        return address == null || address.isEmpty() ? null
                : (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("address"), address);
    }

    public static Specification<Field> getSpecification(FieldFilterDTO filter, User authenticatedUser) {
        return Specification.where(isOwnedByUser(filter.owned(), authenticatedUser))
                .and(matchesEnabled(filter.enabled()))
                .and(hasName(filter.fieldName()))
                .and(hasGroundType(filter.groundType()))
                .and(hasRoof(filter.hasRoof()))
                .and(hasIllumination(filter.hasIllumination()))
                .and(matchesZone(filter.zone()))
                .and(matchesAddress(filter.address()));
    }
}