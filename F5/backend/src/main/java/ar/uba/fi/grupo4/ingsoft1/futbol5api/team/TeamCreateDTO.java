package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.NotNull;

import java.util.function.Supplier;

public record TeamCreateDTO(
        @NotNull String teamName,
        String primaryColor,
        String subColor,
        Ranking ranking
) {
    public TeamCreateDTO(String primaryColor, String subColor, Ranking ranking) {
        this(null, primaryColor, subColor, ranking);
    }

    public Team asTeam(Supplier<User> getOwner) {
        return asTeam(null, getOwner);
    }

    public Team asTeam(Long id, Supplier<User> getOwner) {
        return new Team(
                id,
                teamName,
                primaryColor,
                subColor,
                getOwner.get(),
                ranking
        );
    }
}
