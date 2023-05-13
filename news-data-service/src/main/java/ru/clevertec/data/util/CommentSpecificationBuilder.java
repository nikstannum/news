package ru.clevertec.data.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.clevertec.data.entity.Comment;

@Component
public class CommentSpecificationBuilder {

    public static final String ATTRIBUTE_NEWS_ID = "newsId";
    public static final String ATTRIBUTE_USER_ID = "userId";
    public static final String OPERATION_EQ = "eq";
    public static final String PATTERN_PERCENT = "%";
    public static final String ATTRIBUTE_TEXT = "text";
    private static final String COLON = ":";

    public Specification<Comment> getSpecificationSelectCommentByParams(QueryCommentParams queryParams) {
        Specification<Comment> specUserId = getUserIdSpecification(queryParams);
        Specification<Comment> specNewsId = getNewsIdSpecification(queryParams);
        Specification<Comment> specText = getTextSpec(queryParams);
        return specUserId.and(specNewsId).and(specText);
    }

    private Specification<Comment> getNewsIdSpecification(QueryCommentParams queryParams) {
        return (root, query, cb) -> {
            Long newsId = queryParams.getNews_id();
            if (newsId == null) {
                return null;
            } else {
                return cb.equal(root.get(ATTRIBUTE_NEWS_ID), newsId);
            }
        };
    }


    private Specification<Comment> getUserIdSpecification(QueryCommentParams queryParams) {
        return (root, query, cb) -> {
            Long userId = queryParams.getUser_id();
            if (userId == null) {
                return null;
            } else {
                return cb.equal(root.get(ATTRIBUTE_USER_ID), userId);
            }
        };
    }

    private Specification<Comment> getTextSpec(QueryCommentParams queryParams) {
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
