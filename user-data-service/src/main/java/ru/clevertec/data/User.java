package ru.clevertec.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.clevertec.data.converter.UserRoleConverter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", length = 30)
    @Size(max = 30, message = "Your first name is longer than 30 characters. You can use an alias.")
    private String firstName;

    @Column(name = "last_name", length = 30)
    @Size(max = 30, message = "Your last name is longer than 30 characters. You can use an alias.")
    private String lastName;

    @Column(name = "email", length = 50, nullable = false)
    @Size(max = 50, message = "Your email is longer than 50 characters. You can create a new mailbox and use it.")
    @NotBlank
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 6, message = "Too short password.")
    private String password;

    @Column(name = "role_id", nullable = false)
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User other = (User) o;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public enum UserRole {
        ADMIN, JOURNALIST, SUBSCRIBER
    }
}
