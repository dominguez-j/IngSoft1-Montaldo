package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import java.time.LocalDate;

public record CommentDTO (
    String content,
    int valoration,
    LocalDate date,
    String userName,
    String fieldName
) {
    public CommentDTO(Comment comment) {
        this(
                comment.getContent(),
                comment.getValoration(),
                comment.getDate(),
                comment.getUser().getName(),
                comment.getField().getName()
        );
    }

}
