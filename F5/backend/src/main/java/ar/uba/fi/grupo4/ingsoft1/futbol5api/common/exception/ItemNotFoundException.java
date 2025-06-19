package ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String entity, Long id) {
        super(String.format("Failed to find %s with id %s", entity, id));
    }

    public ItemNotFoundException(String entity) {
        super(String.format("Failed to find %s", entity));
    }
}
