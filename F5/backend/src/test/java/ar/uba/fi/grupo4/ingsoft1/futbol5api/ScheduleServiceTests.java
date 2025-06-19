package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.GroundType;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = Futbol5API.class)
@ActiveProfiles("test")
@Transactional
class ScheduleServiceTests {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;


    private Field field;

    @BeforeEach
    void setUp() {
        User user = new User(
                "test@example.com",
                "password",
                20,
                "aaaaa",
                "EEEEE",
                Gender.NON_BINARY,
                "WIWIWIW"
        );
        userRepository.save(user);
        JwtUserDetails userDetails = new JwtUserDetails(user.getEmail(), user.getRole());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        FieldCreateDTO fieldCreateDTO = new FieldCreateDTO(
                "Cancha 1",
                GroundType.SYNTHETIC_GRASS,
                true,
                false,
                "zona norte",
                "calle falsa 123"
        );
        this.field = fieldCreateDTO.asField(authenticatedUserProvider::getAuthenticatedUser);
        fieldRepository.save(field);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateSchedule() {
        ScheduleCreateDTO dto = new ScheduleCreateDTO(
                "Cancha 1",
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                60
        );

        scheduleService.createSchedule(dto);

        List<Schedule> schedules = scheduleRepository.findSchedulesByField(field);
        Assertions.assertEquals(1, schedules.size());

        Schedule s = schedules.get(0);
        Assertions.assertEquals(DayOfWeek.MONDAY, s.getDayOfWeek());
        Assertions.assertEquals(LocalTime.of(10, 0), s.getOpeningTime());
        Assertions.assertEquals(LocalTime.of(18, 0), s.getClosingTime());
        Assertions.assertEquals(60, s.getSlotDurationMinutes());
    }

    @Test
    void testGetSchedulesByFieldNameReturnsCorrectData() {
        Schedule schedule = new Schedule(
                field,
                DayOfWeek.WEDNESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                30
        );
        scheduleRepository.save(schedule);

        List<ScheduleDTO> result = scheduleService.getSchedulesByFieldName("Cancha 1");

        Assertions.assertEquals(1, result.size());
        ScheduleDTO dto = result.get(0);
        Assertions.assertEquals("Cancha 1", dto.fieldName());
        Assertions.assertEquals(DayOfWeek.WEDNESDAY, dto.dayOfWeek());
        Assertions.assertEquals(LocalTime.of(9, 0), dto.openingTime());
        Assertions.assertEquals(LocalTime.of(12, 0), dto.closingTime());
        Assertions.assertEquals(30, dto.slotDurationMinutes());
    }

    @Test
    void testCreateScheduleWithDuplicateDayAndFieldThrowsException() {
        scheduleService.createSchedule(new ScheduleCreateDTO(
                "Cancha 1",
                DayOfWeek.FRIDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                60
        ));

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            scheduleService.createSchedule(new ScheduleCreateDTO(
                    "Cancha 1",
                    DayOfWeek.FRIDAY,
                    LocalTime.of(14, 0),
                    LocalTime.of(20, 0),
                    60
            ));
        });
    }

    @Test
    void testNonExistentFieldThrowsException() {
        ScheduleCreateDTO dto = new ScheduleCreateDTO(
                "Cancha Inexistente",
                DayOfWeek.TUESDAY,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                60
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.createSchedule(dto);
        });
    }

    @Test
    void testOpeningTimeMustBeBeforeClosingTime() {
        ScheduleCreateDTO dto = new ScheduleCreateDTO(
                "Cancha 1",
                DayOfWeek.MONDAY,
                LocalTime.of(18, 0),
                LocalTime.of(10, 0),
                60
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scheduleService.createSchedule(dto);
        });
    }

    @Test
    void testCreateScheduleWithInvalidSlotDurationThrowsException() {
        ScheduleCreateDTO invalidDto1 = new ScheduleCreateDTO(
                "Cancha 1",
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                0
        );

        ScheduleCreateDTO invalidDto2 = new ScheduleCreateDTO(
                "Cancha 1",
                DayOfWeek.TUESDAY,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                -15
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(invalidDto1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(invalidDto2));
    }
}