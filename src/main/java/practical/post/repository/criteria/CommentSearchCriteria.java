package practical.post.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import practical.post.model.entity.Comment;
import practical.post.model.request.comment.CommentSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CommentSearchCriteria implements Specification<Comment> {

    private final CommentSearchRequest commentSearchRequest;

    @Override
    public Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(commentSearchRequest.getPostId())) {
            predicates.add(criteriaBuilder.equal(root.get("post").get("id"), commentSearchRequest.getPostId()));
        }

        if (Objects.nonNull(commentSearchRequest.getContent())) {
            predicates.add(criteriaBuilder.like(root.get(Comment.CONTENT_FIELD), "%" + commentSearchRequest.getContent() + "%"));
        }

        if (Objects.nonNull(commentSearchRequest.getDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(Comment.DELETED_FIELD), commentSearchRequest.getDeleted()));
        }

        if (Objects.nonNull(commentSearchRequest.getKeyword())) {
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Comment.CONTENT_FIELD), "%" + commentSearchRequest.getKeyword() + "%")
            );

            predicates.add(predicate);
        }

        sort(root, query, criteriaBuilder);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void sort(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if ((Objects.nonNull(commentSearchRequest.getCommentSortField()))) {
            switch (commentSearchRequest.getCommentSortField()) {
                case CONTENT -> query.orderBy(criteriaBuilder.asc(root.get(Comment.CONTENT_FIELD)));
                default -> query.orderBy(criteriaBuilder.asc(root.get(Comment.ID_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.asc(root.get(Comment.ID_FIELD)));
        }
    }
}
