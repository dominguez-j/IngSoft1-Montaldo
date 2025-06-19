package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import org.springframework.lang.Nullable;

public record FieldFilterDTO(
        @Nullable Boolean owned,
        @Nullable Boolean enabled,
        @Nullable String fieldName,
        @Nullable GroundType groundType,
        @Nullable Boolean hasRoof,
        @Nullable Boolean hasIllumination,
        @Nullable String zone,
        @Nullable String address
) {}
