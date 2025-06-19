package ar.uba.fi.grupo4.ingsoft1.futbol5api.user.email;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/verifications")
@Tag(name = "3 - Email Verification")
public class EmailVerificationController {

    private final UserService userService;

    @Autowired
    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Verify a user's account with a token sent by email")
    @ApiResponse(responseCode = "200", description = "Account successfully verified")
    @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content)
    public ResponseEntity<String> verifyEmail(
            @Parameter(description = "Verification token", required = true)
            @RequestParam("token") String token
    ) {
        if (userService.verifyEmailToken(token)) {
            return ResponseEntity.ok("Account successfully verified.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping(value = "/recover-password")
    @Operation(summary = "Send email to recover password")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200", description = "Email sent successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "No user was found with that email", content = @Content)
    public void recoverPassword(@Valid @RequestBody RecoverPasswordDTO data) {
        try {
            userService.sendPasswordResetEmail(data.email());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PostMapping(value = "/reset-password")
    @Operation(summary = "Reset password with token")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content)
    public void resetPassword(@Valid @RequestBody ResetPasswordDTO data) {
        boolean success = userService.resetPassword(data.token(), data.newPassword());
        if (!success) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido o expirado");
        }
    }
}