package ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security;

public record JwtUserDetails (
        String email,
        String role
) {}