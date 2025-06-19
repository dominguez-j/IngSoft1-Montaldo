package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record CommentCreateDTO (
        @Nullable String content,
        @NotNull int valoration,
        @NotNull String fieldName
) {


    public Comment asComment(Function<String, Optional<Field>> getField, Supplier<User> getUser) {
        return asComment(null, getField, getUser);
    }

    public Comment asComment(Long id, Function<String, Optional<Field>> getField, Supplier<User> getUser) {
        var field = getField.apply(fieldName)
                .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldName));
        return new Comment(
                id,
                content,
                valoration,
                LocalDate.now(),
                getUser.get(),
                field
        );
    }
}
