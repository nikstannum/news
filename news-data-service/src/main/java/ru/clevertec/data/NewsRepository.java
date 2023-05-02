package ru.clevertec.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {

    Page<News> findByTitleOrTextContains(String titleKeyWord, String textKeyWord, Pageable pageable);
}
