package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.Ranking;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email.EmailService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.Schedule;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.DayOfWeek;
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


@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = Futbol5API.class)
@ActiveProfiles("test")
@Transactional
public class OpenMatchServiceTests{
    @Autowired
    private OpenMatchService openMatchService;

    @Autowired
    private OpenMatchRepository openMatchRepository;

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
    void testCreateOpenMatch()throws ItemNotFoundException{
        Page<BlockedSlotDTO> blockedSlotPage = blockedSlotService.findAllMyBlockedSlots(true, PageRequest.of(0, 1));
        BlockedSlotDTO blockedSlotDto = blockedSlotPage.getContent().getFirst();
        OpenMatchCreateDTO dto = new OpenMatchCreateDTO(
                2,
                10,
                "field1",
                blockedSlotDto.id()
        );

        openMatchService.createOpenMatch(dto);

        User owner = authenticatedUserProvider.getAuthenticatedUser();

        Page<OpenMatch> page = openMatchRepository.findByOwner(owner,PageRequest.of(0, 1));

        OpenMatch openMatch = page.getContent().getFirst();

        Field field = openMatch.getField();

        BlockedSlot blockedSlot = openMatch.getBlockedSlot();

        Assertions.assertEquals("field1",field.getName());
        Assertions.assertEquals(2,openMatch.getMinPlayers());
        Assertions.assertEquals(10,openMatch.getMaxPlayers());
        Assertions.assertEquals(blockedSlotDto.id(), blockedSlot.getId());

    }
}

