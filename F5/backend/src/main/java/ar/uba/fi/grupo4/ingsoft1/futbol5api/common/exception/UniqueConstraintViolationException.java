package ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception;

public class UniqueConstraintViolationException extends RuntimeException {
    public UniqueConstraintViolationException(String message) {
        super(message);
    }
}
