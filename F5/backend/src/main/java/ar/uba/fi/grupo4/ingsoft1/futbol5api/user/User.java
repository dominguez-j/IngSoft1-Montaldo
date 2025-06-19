package ar.uba.fi.grupo4.ingsoft1.futbol5api.user;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.comment.Comment;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Table(name = "users")
public class User implements UserDetails, UserCredentials {

    @Id
    @GeneratedValue
    private Long id;

    @Transient
    @JsonIgnore
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = false)
    private String password;

    @Column(unique = false, nullable = false)
    private int age;

    @Column(unique = false, nullable = false)
    private String name;

    @Column(unique = false, nullable = false)
    private String surname;

    @Column(unique = false, nullable = false)
    private Gender gender;

    @Column(unique = false, nullable = false)
    private String zone;

    @Column(unique = false, nullable = false)
    private String role;

    @Column(unique = false, nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Field> fields;

    @OneToMany(mappedBy = "blockOwner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BlockedSlot> blockedSlots;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;

    public User() {}

    public User(
            String username,
            String password,
            int age,
            String name,
            String surname,
            Gender gender,
            String zone
    ) {
        this.username = username;
        this.email = username;
        this.password = password;
        this.age = age;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.zone = zone;

        this.role = "ROLE_USER";
        this.enabled = false;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String email() {
        return this.email;
    }

    @Override
    public String password() {
        return this.password;
    }


    public int getAge() {
        return this.age;
    }

    public String getName() {
        return this.name;
    }

    public Gender getGender() {
        return this.gender;
    }

    public String getZone() {
        return this.zone;
    }

    public String getRole() {
        return this.role;
    }

    public String getEmail() {
        return this.email;
    }

    public boolean getEnabled() { return this.enabled; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPassword(String password) { this.password = password; }

    public List<Field> getFields() {
        return fields;
    }

    public List<BlockedSlot> getBlockedSlots() {
        return blockedSlots;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setBlockedSlots(List<BlockedSlot> blockedSlots) {
        this.blockedSlots = blockedSlots;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
