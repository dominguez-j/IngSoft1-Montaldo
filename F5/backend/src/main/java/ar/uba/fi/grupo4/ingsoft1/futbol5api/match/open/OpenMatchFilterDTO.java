package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;

import org.springframework.lang.Nullable;
import java.time.LocalDate;

public record OpenMatchFilterDTO(
        @Nullable Boolean owned,
        @Nullable String fieldName,
        @Nullable LocalDate date
) {
}