package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;

public record FieldDTO (
        Long id,
        String name,
        boolean enabled,
        GroundType groundType,
        boolean hasRoof,
        boolean hasIllumination,
        String zone,
        String address
) {
    FieldDTO(Field field) {
        this(
                field.getId(),
                field.getName(),
                field.getEnabled(),
                field.getGroundType(),
                field.getHasRoof(),
                field.getHasIllumination(),
                field.getZone(),
                field.getAddress()
        );
    }
}
