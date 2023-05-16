package ru.clevertec.data;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import ru.clevertec.data.entity.Comment;
/**
 * Repository interface for interacting with the database.
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    /**
     * Getting comments by news ID. The list of news is displayed in paginated mode
     *
     * @param id news id
     * @param pageable paginated parameters
     * @return list of comments
     */
    List<Comment> findByNewsId(Long id, Pageable pageable);
}
