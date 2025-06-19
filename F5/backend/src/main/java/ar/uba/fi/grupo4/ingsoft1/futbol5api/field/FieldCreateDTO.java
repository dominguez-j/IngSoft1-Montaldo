package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.function.Supplier;

public record FieldCreateDTO (
        @NotBlank String name,
        @NotNull GroundType groundType,
        @NotNull Boolean hasRoof,
        @NotNull Boolean hasIllumination,
        @NotBlank String zone,
        @NotBlank String address
) {
    public Field asField(Supplier<User> getOwner) {
        User owner = getOwner.get();
        return new Field(null, name, true, groundType, hasRoof, hasIllumination, zone, address, owner);
    }
}
