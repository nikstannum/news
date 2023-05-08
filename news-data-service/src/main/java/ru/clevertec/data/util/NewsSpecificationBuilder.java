package ru.clevertec.data.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.clevertec.data.entity.News;

@Component
public class NewsSpecificationBuilder {
    public static final String ATTRIBUTE_USER_ID = "userId";
    public static final String COLON = ":";
    public static final String OPERATION_EQ = "eq";
    public static final String ATTRIBUTE_TITLE = "title";
    public static final String PATTERN_PERCENT = "%";
    public static final String ATTRIBUTE_TEXT = "text";

    public Specification<News> getSpecificationSelectNewsByParams(NewsQueryParams queryParams) {
        Specification<News> specUserId = getUserIdSpecification(queryParams);
        Specification<News> specTitle = getTitleSpecification(queryParams);
        Specification<News> specText = getTextSpecification(queryParams);
        return specUserId.and(specTitle).and(specText);
    }

    private Specification<News> getUserIdSpecification(NewsQueryParams queryParams) {
        return (root, query, cb) -> {
            Long userId = queryParams.getUser_id();
            if (userId == null) {
                return null;
            } else {
                return cb.equal(root.get(ATTRIBUTE_USER_ID), userId);
            }
        };
    }

    private Specification<News> getTitleSpecification(NewsQueryParams queryParams) {
        return (root, query, cb) -> {
            String paramTitle = queryParams.getTitle();
            if (paramTitle == null) {
                return null;
            }
            String[] operationValue = paramTitle.split(COLON);
            String operation = operationValue[0];
            String value = operationValue[1];
            if (operation.equalsIgnoreCase(OPERATION_EQ)) {
                return cb.equal(root.get(ATTRIBUTE_TITLE), value);
            } else {
                return cb.like(root.get(ATTRIBUTE_TITLE), PATTERN_PERCENT + value + PATTERN_PERCENT);
            }
        };
    }

    private Specification<News> getTextSpecification(NewsQueryParams queryParams) {
        return (root, query, cb) -> {
            String paramText = queryParams.getText();
            if (paramText == null) {
                return null;
            }
            String[] operationValue = paramText.split(COLON);
            String operation = operationValue[0];
            String value = operationValue[1];
            if (operation.equalsIgnoreCase(OPERATION_EQ)) {
                return cb.equal(root.get(ATTRIBUTE_TEXT), value);
            } else {
                return cb.like(root.get(ATTRIBUTE_TEXT), PATTERN_PERCENT + value + PATTERN_PERCENT);
            }
        };
    }
}
