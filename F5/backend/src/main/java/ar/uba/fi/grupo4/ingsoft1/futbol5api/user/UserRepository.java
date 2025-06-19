package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {;
    Optional<User> findByEmail(String email);
}
