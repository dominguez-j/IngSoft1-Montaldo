package ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
}
