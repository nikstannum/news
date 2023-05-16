package ru.clevertec.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.clevertec.data.entity.News;
/**
 * Repository interface for interacting with the database.
 */
public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    /**
     * Method for searching news by a keyword contained in the news title or its text
     * @param titleKeyWord keyword for news title
     * @param textKeyWord keyword for news text
     * @param pageable paginated parameters
     * @return news page
     */
    Page<News> findByTitleContainsOrTextContains(String titleKeyWord, String textKeyWord, Pageable pageable);

}
