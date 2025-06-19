package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.function.Supplier;

public record FieldUpdateDTO(
        @NotBlank String name,
        @NotNull Boolean enabled,
        @NotNull GroundType groundType,
        @NotNull Boolean hasRoof,
        @NotNull Boolean hasIllumination,
        @NotBlank String zone,
        @NotBlank String address
) {
    public Field asField(Long id, Supplier<User> getOwner) {
        User owner = getOwner.get();
        return new Field(id, name, enabled, groundType, hasRoof, hasIllumination, zone, address, owner);
    }
}
