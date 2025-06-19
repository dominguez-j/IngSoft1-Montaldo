package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.Ranking;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email.EmailService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = Futbol5API.class)
@ActiveProfiles("test")
@Transactional
public class ClosedMatchServiceTests {
    @Autowired
    private ClosedMatchService closedMatchService;

    @Autowired
    private ClosedMatchRepository closedMatchRepository;

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private BlockedSlotService blockedSlotService;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ScheduleService scheduleService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        User user = new User(
                "user@example.com",
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

        TeamCreateDTO dtoSecondTeam = new TeamCreateDTO(
                "teamB",
                "red",
                "white",
                Ranking.BEGINNER
        );

        teamService.createTeam(dtoSecondTeam);

        fieldService.createField(
                new FieldCreateDTO(
                        "field1",
                        GroundType.SYNTHETIC_GRASS,
                        true,
                        false,
                        "zone1",
                        "fake street 123"
                )
        );
        LocalDate date = LocalDate.now().plusDays(1);

        scheduleService.createSchedule(new ScheduleCreateDTO(
                "field1",
                date.getDayOfWeek(),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                60
        ));

        blockedSlotService.createBlockedSlot(new BlockedSlotCreateDTO(
                "field1",
                date,
                0,
                "string")
        );

    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateClosedMatch() throws ItemNotFoundException {
        Page<BlockedSlotDTO> blockedSlotPage = blockedSlotService.findAllMyBlockedSlots(true, PageRequest.of(0, 1));
        BlockedSlotDTO blockedSlotDto = blockedSlotPage.getContent().getFirst();
        ClosedMatchCreateDTO dto = new ClosedMatchCreateDTO(
                "field1",
                "teamA",
                "teamB",
                blockedSlotDto.id()
        );
        closedMatchService.createClosedMatch(dto);

        User owner = authenticatedUserProvider.getAuthenticatedUser();

        Page<ClosedMatch> page = closedMatchRepository.findByOwner(owner,PageRequest.of(0, 1));

        ClosedMatch closedMatch = page.getContent().getFirst();

        Field field = closedMatch.getField();

        BlockedSlot blockedSlot = closedMatch.getBlockedSlot();

        Assertions.assertEquals("teamA", closedMatch.getTeamA().getTeamName());
        Assertions.assertEquals("teamB", closedMatch.getTeamB().getTeamName());
        Assertions.assertEquals("field1", field.getName());
        Assertions.assertEquals(blockedSlotDto.id(), blockedSlot.getId());

    }

    @Test
    void testAddingNonExistingTeamThrowsException() {
        Page<BlockedSlotDTO> blockedSlotPage = blockedSlotService.findAllMyBlockedSlots(true, PageRequest.of(0, 1));
        BlockedSlotDTO blockedSlotDto = blockedSlotPage.getContent().getFirst();
        ClosedMatchCreateDTO dto = new ClosedMatchCreateDTO(
                "field1",
                "nonExistingTeam",
                "teamB",
                blockedSlotDto.id()
        );
        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            closedMatchService.createClosedMatch(dto);
        });
    }

    @Test
    void testAddingNonExistingFieldThrowsException() throws ItemNotFoundException {
        Page<BlockedSlotDTO> blockedSlotPage = blockedSlotService.findAllMyBlockedSlots(true, PageRequest.of(0, 1));
        BlockedSlotDTO blockedSlotDto = blockedSlotPage.getContent().getFirst();
        ClosedMatchCreateDTO dto = new ClosedMatchCreateDTO(
                "nonExistingField",
                "teamA",
                "teamB",
                blockedSlotDto.id()
        );
        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            closedMatchService.createClosedMatch(dto);
        });

    }

    @Test
    void testNonExistingBlockedSlotThrowsException() {
        ClosedMatchCreateDTO dto = new ClosedMatchCreateDTO(
                "field1",
                "teamA",
                "teamB",
                9999L
        );
        Exception ex = assertThrows(ItemNotFoundException.class, () -> {
            closedMatchService.createClosedMatch(dto);
        });
    }

    @Test
    void testBlockedSlotDoesNotBelongToFieldThrowsException() {
        fieldService.createField(
                new FieldCreateDTO(
                        "field2",
                        GroundType.SYNTHETIC_GRASS,
                        true,
                        false,
                        "zone2",
                        "another street"
                )
        );

        scheduleService.createSchedule(new ScheduleCreateDTO(
                "field2",
                LocalDate.now().getDayOfWeek(),
                LocalTime.of(10, 0),
                LocalTime.of(16, 0),
                60
        ));

        BlockedSlotDTO otherSlot = blockedSlotService.createBlockedSlot(new BlockedSlotCreateDTO(
                "field2",
                LocalDate.now(),
                1,
                "Otro motivo"
        ));

        ClosedMatchCreateDTO dto = new ClosedMatchCreateDTO(
                "field1",
                "teamA",
                "teamB",
                otherSlot.id()
        );

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            closedMatchService.createClosedMatch(dto);
        });
    }




}
