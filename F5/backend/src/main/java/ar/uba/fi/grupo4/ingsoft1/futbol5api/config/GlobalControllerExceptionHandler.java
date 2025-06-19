package ar.uba.fi.grupo4.ingsoft1.futbol5api.config;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.BusinessRuleException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid arguments supplied",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "Validation failed because x, y, z")
            )
    )
    public ResponseEntity<String> handleMethodArgumentInvalid(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ItemNotFoundException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "404",
            description = "Referenced entity not found",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "Failed to find foo with id 42")
            )
    )
    public ResponseEntity<String> handleItemNotFound(ItemNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalArgumentException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "400",
            description = "Arguments given were incorrect or insufficient",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "Missing argument: foo")
            )
    )
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(responseCode = "403", description = "Invalid jwt access token supplied", content = @Content)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleFallback(Throwable ex) {
        return new ResponseEntity<>(
                ex.getClass().getCanonicalName() + " " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(value = UniqueConstraintViolationException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "400",
            description = "Business rule violated when trying to create a resource multiple times",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "Field name already registered")
            )
    )
    public ResponseEntity<String> handleUniqueConstraintViolationException(UniqueConstraintViolationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PermissionDeniedException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "403",
            description = "Business rule violated when trying to access a resource that the user is not allowed to",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "User does not own field with name ...")
            )
    )
    public ResponseEntity<String> handlePermissionDeniedException(PermissionDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BusinessRuleException.class, produces = "text/plain")
    @ApiResponse(
            responseCode = "400",
            description = "Business rule violated when trying to join a match",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "User is already registered in this match.")
            )
    )
    public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}