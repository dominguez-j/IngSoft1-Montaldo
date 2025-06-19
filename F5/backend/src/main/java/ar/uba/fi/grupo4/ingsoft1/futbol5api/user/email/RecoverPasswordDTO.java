package ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecoverPasswordDTO(
        @NotBlank @Email String email
) {}