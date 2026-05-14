package practical.post.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import practical.post.model.entity.Post;
import practical.post.model.request.post.PostSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class PostSearchCriteria implements Specification<Post> {

    private final PostSearchRequest postSearchRequest;

    @Override
    public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(postSearchRequest.getTitle())) {
            predicates.add(criteriaBuilder.like(root.get(Post.TITLE_FIELD), "%" + postSearchRequest.getTitle() + "%"));
        }

        if (Objects.nonNull(postSearchRequest.getContent())) {
            predicates.add(criteriaBuilder.like(root.get(Post.CONTENT_FIELD), "%" + postSearchRequest.getContent() + "%"));
        }

        if (Objects.nonNull(postSearchRequest.getDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(Post.DELETED_FIELD), postSearchRequest.getDeleted()));
        }

        if (Objects.nonNull(postSearchRequest.getKeyword())) {
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Post.TITLE_FIELD), "%" + postSearchRequest.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Post.CONTENT_FIELD), "%" + postSearchRequest.getKeyword() + "%")
            );

            predicates.add(predicate);
        }

        sort(root, query, criteriaBuilder);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void sort(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if ((Objects.nonNull(postSearchRequest.getPostSortField()))) {
            switch (postSearchRequest.getPostSortField()) {
                case TITLE -> query.orderBy(criteriaBuilder.asc(root.get(Post.TITLE_FIELD)));
                case CONTENT -> query.orderBy(criteriaBuilder.asc(root.get(Post.CONTENT_FIELD)));
                default -> query.orderBy(criteriaBuilder.asc(root.get(Post.ID_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.asc(root.get(Post.ID_FIELD)));
        }
    }
}
