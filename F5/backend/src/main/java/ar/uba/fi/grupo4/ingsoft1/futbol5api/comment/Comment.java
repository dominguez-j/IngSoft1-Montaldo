package ar.uba.fi.grupo4.ingsoft1.futbol5api.comment;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity(name = "comment")
@Table(
        name = "comments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"field_id", "user_id"})
        }
)
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "content", nullable = true)
    private String content;

    @Column(name = "valoration", nullable = false)
    private int valoration;

    // YYYY-MM-DD
    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    @JsonIgnore
    private Field field;

    public Comment() {}

    public Comment(String content, int valoration, LocalDate date, User user, Field field) {
        this(
                null,
                content,
                valoration,
                date,
                user,
                field
        );
    }

    public Comment(Long id, String content, int valoration, LocalDate date, User user, Field field) {
        this.content = content;
        this.valoration = valoration;
        this.date = date;
        this.user = user;
        this.field = field;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setValoration(int valoration) {
        this.valoration = valoration;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getValoration() {
        return valoration;
    }

    public LocalDate getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public Field getField() {
        return field;
    }
}