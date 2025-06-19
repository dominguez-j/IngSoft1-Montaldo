package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.function.Function;

public record UserCreateDTO(
        @NotBlank @Email String email,
        @NotBlank String password,
        @Min(14) int age,
        @NotBlank String name,
        @NotBlank String surname,
        @NotNull Gender gender,
        @NotBlank String zone
) implements UserCredentials {
    public User asUser(Function<String, String> encryptPassword) {
        return new User(email,
                encryptPassword.apply(password),
                age,
                name,
                surname,
                gender,
                zone);
    }
}
