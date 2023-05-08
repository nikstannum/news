package ru.clevertec.data;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.clevertec.data.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    @Query(value = """
            SELECT c.id, c.news_id, c.user_id, c.text, c.create_time
            FROM comments c
            WHERE c.news_id = :id
            """,
            nativeQuery = true)
    List<Comment> findByNewsId(@Param("id") Long id, Pageable pageable);
}
