package ru.clevertec.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "text")
    private String text;

    @Generated(GenerationTime.INSERT)
    @Column(name = "create_time", insertable = false, updatable = false)
    private Instant createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Comment other = (Comment) o;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
