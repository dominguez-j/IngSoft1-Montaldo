package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;


public record UserDTO (
        String name,
        String surname,
        String email
) {
    public UserDTO(User user) {
        this(user.getName(), user.getSurname(), user.getEmail());
    }
}
