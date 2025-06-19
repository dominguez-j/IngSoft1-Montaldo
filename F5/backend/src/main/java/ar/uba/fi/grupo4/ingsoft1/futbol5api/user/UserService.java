package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.config.security.JwtUserDetails;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email.EmailService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.refresh_token.RefreshToken;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    @Value("${APP_FRONTEND_BASE_URL}")
    private String frontendBaseUrl;

    @Autowired
    UserService(
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            EmailService emailService
    ) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> {
                    var msg = String.format("Email '%s' not found", username);
                    return new UsernameNotFoundException(msg);
                });
    }

    Optional<TokenDTO> createUser(UserCreateDTO data) {
        if (userRepository.findByEmail(data.email()).isPresent()) {
            return loginUser(data);
        } else {
            var user = data.asUser(passwordEncoder::encode);
            userRepository.save(user);
            return Optional.of(generateTokens(user));
        }
    }

    public void registerUser(UserCreateDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = dto.asUser(passwordEncoder::encode);
        user.setEnabled(false);
        userRepository.save(user);

        String token = jwtService.createEmailVerificationToken(user.getEmail(), 1000 * 60 * 60 * 24); // 24h
        String verificationLink = "http://localhost/auth/html/verification.html?token=" + token;

        String htmlContent = "<p>Gracias por registrarte.</p>" +
                "<p>Por favor haz click acá para verificar tu cuenta:</p>" +
                "<p><a href='" + verificationLink + "'>Verificar cuenta</a> </p>";

        emailService.sendEmail(
                user.getEmail(),
                "Verificá tu cuenta",
                htmlContent
        );
    }

    public boolean verifyEmailToken(String token) {
        return jwtService.extractEmailFromVerificationToken(token)
                .flatMap(userRepository::findByEmail)
                .map(user -> {
                    if (!user.getEnabled()) {
                        user.setEnabled(true);
                        userRepository.save(user);
                    }
                    return true;
                }).orElse(false);
    }

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user was found with that email address."));

        String token = jwtService.createPasswordResetToken(email, 1000 * 60 * 60); // 1h
        String recoverLink = "http://localhost/auth/html/new-password.html?token=" + token;

        String htmlContent = "<p>Hacé clic en el siguiente enlace para restablecer tu contraseña:</p>" +
                "<p><a href=\"" + recoverLink + "\">Recuperar contraseña</a></p>";

        emailService.sendEmail(user.getEmail(), "Recuperar contraseña", htmlContent);
    }

    public boolean resetPassword(String token, String newPassword) {
        return jwtService.extractEmailFromPasswordResetToken(token)
                .flatMap(userRepository::findByEmail)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }

    public Optional<TokenDTO> loginUser(UserCredentials data) {
        Optional<User> maybeUser = userRepository.findByEmail(data.email());

        if (maybeUser.isEmpty()) return Optional.empty();

        User user = maybeUser.get();
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            return Optional.empty();
        }

        if (!user.getEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not validated");
        }

        return Optional.of(generateTokens(user));
    }

    Optional<TokenDTO> refresh(RefreshDTO data) {
        return refreshTokenService.findByValue(data.refreshToken())
                .map(RefreshToken::user)
                .map(this::generateTokens);
    }

    private TokenDTO generateTokens(User user) {
        String accessToken = jwtService.createToken(new JwtUserDetails(
                user.getUsername(),
                user.getRole()
        ));
        RefreshToken refreshToken = refreshTokenService.createFor(user);
        return new TokenDTO(accessToken, refreshToken.value());
    }
}
