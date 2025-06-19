package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import jakarta.validation.constraints.NotBlank;

public record RefreshDTO(
        @NotBlank String refreshToken
) {}
