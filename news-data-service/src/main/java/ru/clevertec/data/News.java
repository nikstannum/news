package ru.clevertec.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "title", nullable = false, columnDefinition = "varchar(150)")
    @Size(max = 150, message = "Too long header of article. Max length should be 150 characters.")
    private String title;

    @Column(name = "text")
    private String text;

    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time", insertable = false, updatable = false)
    private Instant time;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        News other = (News) o;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                '}';
    }
}
