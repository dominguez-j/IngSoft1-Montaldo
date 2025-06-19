package ar.uba.fi.grupo4.ingsoft1.futbol5api.user.refresh_token;

import org.springframework.data.jpa.repository.JpaRepository;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
