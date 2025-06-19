package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.GroundType;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.Schedule;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotCreateDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(classes = Futbol5API.class)
@ActiveProfiles("test")
@Transactional
public class BlockedSlotServiceTests {

    @Autowired
    private BlockedSlotService blockedSlotService;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    private Field field;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user@example.com", "password", 22, "Juan", "Perez", Gender.MAN, "zona sur");
        userRepository.save(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(new JwtUserDetails(user.getEmail(), user.getRole()), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        FieldCreateDTO fieldCreateDTO = new FieldCreateDTO("Cancha Test", GroundType.SYNTHETIC_GRASS, true, true, "zona oeste", "falsa 456");
        field = fieldCreateDTO.asField(authenticatedUserProvider::getAuthenticatedUser);
        fieldRepository.save(field);

        DayOfWeek tomorrowDayOfWeek = LocalDate.now().plusDays(1).getDayOfWeek();
        Schedule schedule = new Schedule(field, tomorrowDayOfWeek, LocalTime.of(9, 0), LocalTime.of(12, 0), 60);
        scheduleRepository.save(schedule);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateValidBlockedSlot() {
        BlockedSlotCreateDTO dto = new BlockedSlotCreateDTO("Cancha Test", LocalDate.now().plusDays(1), 1, "Mantenimiento");
        BlockedSlotDTO result = blockedSlotService.createBlockedSlot(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Cancha Test", result.fieldName());
        Assertions.assertEquals("Juan", result.ownerName());
        Assertions.assertEquals(LocalTime.of(10, 0), result.startTime());
        Assertions.assertEquals(LocalTime.of(11, 0), result.endTime());
        Assertions.assertEquals("Mantenimiento", result.reason());
    }

    @Test
    void testNonExistentFieldThrowsException() {
        BlockedSlotCreateDTO dto = new BlockedSlotCreateDTO("Inexistente", LocalDate.now().plusDays(1), 0, "Otro");
        Assertions.assertThrows(IllegalArgumentException.class, () -> blockedSlotService.createBlockedSlot(dto));
    }

    @Test
    void testNonExistentScheduleThrowsException() {
        BlockedSlotCreateDTO dto = new BlockedSlotCreateDTO("Cancha Test", LocalDate.now().plusDays(3), 0, "Sin horario");
        Assertions.assertThrows(IllegalArgumentException.class, () -> blockedSlotService.createBlockedSlot(dto));
    }

    @Test
    void testInvalidSlotNumberThrowsException() {
        BlockedSlotCreateDTO dto = new BlockedSlotCreateDTO("Cancha Test", LocalDate.now().plusDays(1), 5, "Slot inválido");
        Assertions.assertThrows(IllegalArgumentException.class, () -> blockedSlotService.createBlockedSlot(dto));
    }

    @Test
    void testDuplicateSlotThrowsException() {
        BlockedSlotCreateDTO dto = new BlockedSlotCreateDTO("Cancha Test", LocalDate.now().plusDays(1), 0, "Duplicado");
        blockedSlotService.createBlockedSlot(dto);

        Assertions.assertThrows(UniqueConstraintViolationException.class, () -> blockedSlotService.createBlockedSlot(dto));
    }
}
