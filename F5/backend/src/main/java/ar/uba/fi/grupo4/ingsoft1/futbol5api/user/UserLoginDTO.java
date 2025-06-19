package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        @NotBlank String email,
        @NotBlank String password
) implements UserCredentials {}
