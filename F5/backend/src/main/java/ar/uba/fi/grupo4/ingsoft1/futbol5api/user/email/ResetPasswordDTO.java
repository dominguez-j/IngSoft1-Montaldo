package ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @NotBlank String token,
        @NotBlank String newPassword
) {}