package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import jakarta.validation.constraints.NotNull;

public record TokenDTO(
        @NotNull String accessToken,
        String refreshToken
) {
}
