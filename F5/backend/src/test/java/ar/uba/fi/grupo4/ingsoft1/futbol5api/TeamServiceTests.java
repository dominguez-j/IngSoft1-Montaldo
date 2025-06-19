package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = Futbol5API.class)
@ActiveProfiles("test")
@Transactional
public class TeamServiceTests {
    @Autowired
    private TeamService teamService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        User user = new User(
                "teamuser@example.com",
                "password",
                25,
                "Test",
                "User",
                Gender.MAN,
                "Palermo"
        );
        userRepository.save(user);

        JwtUserDetails userDetails = new JwtUserDetails(user.getEmail(), user.getRole());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        TeamCreateDTO dtoFirstTeam = new TeamCreateDTO(
                "teamA",
                "red",
                "white",
                Ranking.BEGINNER
        );
        teamService.createTeam(dtoFirstTeam);

    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateTeam() {
        TeamCreateDTO dto = new TeamCreateDTO(
                "teamRocket",
                "red",
                "white",
                Ranking.BEGINNER);

        teamService.createTeam(dto);

        Page<TeamDTO> page = teamService.getTeams(
                true,
                false,
                false,
                PageRequest.of(0, 2)
        );

        TeamDTO result = page.getContent().getLast();

        Assertions.assertEquals("teamRocket", result.teamName());
        Assertions.assertEquals("red", result.primaryColor());
        Assertions.assertEquals("white", result.subColor());
        Assertions.assertEquals(Ranking.BEGINNER, result.ranking());
        Assertions.assertEquals("Test",result.ownerName());
        Assertions.assertEquals(0, result.members().size());
    }
    
    @Test
    void testDuplicatedTeamNameThrowsException() throws  Exception {
        TeamCreateDTO dto = new TeamCreateDTO(
                "teamA",
                "red",
                "white",
                Ranking.BEGINNER
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            teamService.createTeam(dto);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("team name"), "Field name already registered");

    }

    @Test
    void testUserInMultipleTeamsThrowsException() throws  Exception {
        TeamCreateDTO dtoSecondTeam = new TeamCreateDTO(
                "teamB",
                "blue",
                "yellow",
                Ranking.BEGINNER
        );

        teamService.createTeam(dtoSecondTeam);

        teamService.addMember("teamA","teamuser@example.com");
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,()->teamService.addMember("teamB","teamuser@example.com"));
        Assertions.assertEquals("User is already a member of another team.", ex.getMessage());
    }

    @Test
    void testUserInSameTeamMultipleTimesThrowsException() throws  Exception {
        teamService.addMember("teamA","teamuser@example.com");
        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,()->teamService.addMember("teamA","teamuser@example.com"));
        Assertions.assertEquals("User is already a member of this team.", ex.getMessage());
    }

    @Test
    void testAddNonExistentUserThrowsException() {
        Exception ex = assertThrows(ResponseStatusException.class, () -> {
            teamService.addMember("teamA", "noexiste@mail.com");
        });

        assertTrue(ex.getMessage().contains("User not found: noexiste@mail.com"));
    }

    @Test
    void testNonOwnerUserTriesToEditTeamThrowsException() throws Exception {
        TeamCreateDTO updateDTO = new TeamCreateDTO(
                "teamA",
                "blue",
                "green",
                Ranking.INTERMEDIATE
        );

        User user2 = new User(
                "man@example.com",
                "password",
                30,
                "Non",
                "Owner",
                Gender.NON_BINARY,
                "Palermo"
        );
        userRepository.save(user2);
        JwtUserDetails userDetails2 = new JwtUserDetails(user2.getEmail(), user2.getRole());
        Authentication auth2 = new UsernamePasswordAuthenticationToken(userDetails2, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth2);

        Exception ex = assertThrows(SecurityException.class, () -> {
            teamService.updateTeam("teamA", updateDTO);
        });

        assertEquals("You are not the captain of this team", ex.getMessage());
    }

    @Test
    void testNonOwnerUserTriesToAddMemberThrowsException() throws Exception {
        User user2 = new User(
                "man@example.com",
                "password",
                30,
                "Non",
                "Owner",
                Gender.NON_BINARY,
                "Palermo"
        );
        userRepository.save(user2);

        JwtUserDetails userDetails2 = new JwtUserDetails(user2.getEmail(), user2.getRole());
        Authentication auth2 = new UsernamePasswordAuthenticationToken(userDetails2, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth2);

        Exception ex = assertThrows(SecurityException.class, () -> {
            teamService.addMember("teamA", user2.getEmail());});

        Assertions.assertEquals("You are not the captain of this team", ex.getMessage());
    }

    @Test
    void testNonOwnerUserTriesToRemoveMemberThrowsException() throws Exception {
        User user2 = new User(
                "man@example.com",
                "password",
                30,
                "Non",
                "Owner",
                Gender.NON_BINARY,
                "Palermo"
        );
        userRepository.save(user2);

        JwtUserDetails userDetails2 = new JwtUserDetails(user2.getEmail(), user2.getRole());
        Authentication auth2 = new UsernamePasswordAuthenticationToken(userDetails2, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth2);

        Exception ex = assertThrows(SecurityException.class, () -> {
            teamService.removeMember("teamA", user2.getEmail());});

        Assertions.assertEquals("You are not the captain of this team", ex.getMessage());
    }

    @Test
    void testVolumeCreateManyTeams() {
        int cantidadEquipos = 1000;

        for (int i = 0; i < cantidadEquipos; i++) {
            TeamCreateDTO dto = new TeamCreateDTO(
                    "team" + i,
                    "color" + i,
                    "subcolor" + i,
                    Ranking.BEGINNER
            );
            teamService.createTeam(dto);
        }

        Page<TeamDTO> equipos = teamService.getTeams(
                true,
                false,
                false,
                PageRequest.of(0, cantidadEquipos)
        );
        
        Assertions.assertEquals(cantidadEquipos + 1, equipos.getTotalElements());
    }

}
