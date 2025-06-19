package ar.uba.fi.grupo4.ingsoft1.futbol5api.team;
import java.util.List;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserDTO;


public record TeamDTO(
        String teamName,
        String primaryColor,
        String subColor,
        String ownerName,
        Ranking ranking,
        List<UserDTO> members
) {
    public TeamDTO(Team team, boolean includeMembers) {
        this(
                team.getTeamName(),
                team.getPrimaryColor(),
                team.getSubColor(),
                team.getTeamOwner().getName(),
                team.getRanking(),
                includeMembers
                        ? team.getMembers().stream().map(UserDTO::new).toList()
                        : List.of()
        );
    }
}
