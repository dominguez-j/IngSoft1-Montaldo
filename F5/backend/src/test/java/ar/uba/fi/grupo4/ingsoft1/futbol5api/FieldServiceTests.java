package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.*;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.Gender;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class FieldServiceTests {
	@Autowired
	private FieldService fieldService;

	@Autowired
	private UserRepository userRepository;

	@MockitoBean
	private EmailService emailService;

	private void loadSomeFields() {
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
		fieldService.createField(
				new FieldCreateDTO(
						"field2",
						GroundType.CONCRETE,
						false,
						false,
						"zone2",
						"fake street 234"
				)
		);
		fieldService.createField(
				new FieldCreateDTO(
						"field3",
						GroundType.SAND,
						true,
						true,
						"zone3",
						"fake street 345"
				)
		);
	}

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

		loadSomeFields();
	}

	@AfterEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void testCreateFieldAsAuthenticatedUser() {
		Page<FieldDTO> page = fieldService.getFields(
				new FieldFilterDTO(
						null,
						null,
						"field1",
						null,
						null,
						null,
						null,
						null
				),
				PageRequest.of(0, 1)
		);

		FieldDTO result = page.getContent().getFirst();

		Assertions.assertEquals("field1", result.name());
		Assertions.assertTrue(result.enabled());
		Assertions.assertTrue(result.hasRoof());
		Assertions.assertFalse(result.hasIllumination());
		Assertions.assertEquals("zone1", result.zone());
		Assertions.assertEquals(GroundType.SYNTHETIC_GRASS, result.groundType());
		Assertions.assertEquals("fake street 123", result.address());
	}

	@Test
	void testCreateFieldWithDuplicateNameThrowsException() throws Exception {
		UniqueConstraintViolationException ex = Assertions.assertThrows(
				UniqueConstraintViolationException.class,
				() -> fieldService.createField(new FieldCreateDTO(
						"field1",
						GroundType.SYNTHETIC_GRASS,
						true,
						false,
						"zone2",
						"another address"
				))
		);

		Assertions.assertEquals("Field name already registered", ex.getMessage());
	}

	@Test
	void testDeleteField() {
		Assertions.assertDoesNotThrow(() -> fieldService.deleteField("field1"));
		Page<FieldDTO> page = fieldService.getFields(
				new FieldFilterDTO(
						null,
						null,
						"field1",
						null,
						null,
						null,
						null,
						null
				),
				PageRequest.of(0, 1)
		);
		Assertions.assertTrue(page.isEmpty());
	}

	@Test
	void testUpdateField() {
		Assertions.assertDoesNotThrow(() -> fieldService.updateField(
				"field1",
				new FieldUpdateDTO(
						"newfield1",
						false,
						GroundType.SYNTHETIC_GRASS,
						true,
						true,
						"zone1",
						"fake address 123"
				)
				)
		);
		Page<FieldDTO> page = fieldService.getFields(
				new FieldFilterDTO(
						null,
						null,
						"newfield1",
						null,
						null,
						null,
						null,
						null
				),
				PageRequest.of(0, 1)
		);

		FieldDTO result = page.getContent().getFirst();

		Assertions.assertEquals("newfield1", result.name());
		Assertions.assertFalse(result.enabled());
		Assertions.assertTrue(result.hasRoof());
		Assertions.assertTrue(result.hasIllumination());
		Assertions.assertEquals("zone1", result.zone());
		Assertions.assertEquals(GroundType.SYNTHETIC_GRASS, result.groundType());
		Assertions.assertEquals("fake address 123", result.address());

		Page<FieldDTO> emptyPage = fieldService.getFields(
				new FieldFilterDTO(
						null,
						null,
						"field1",
						null,
						null,
						null,
						null,
						null
				),
				PageRequest.of(0, 1)
		);
		Assertions.assertTrue(emptyPage.isEmpty());
	}
}
