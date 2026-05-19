package practical.post.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import practical.post.model.entity.User;
import practical.post.model.request.user.UserSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class UserSearchCriteria implements Specification<User> {

    private final UserSearchRequest userSearchRequest;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(userSearchRequest.getEmail())) {
            predicates.add(criteriaBuilder.like(root.get(User.EMAIL_FIELD), "%" + userSearchRequest.getEmail() + "%"));
        }

        if (Objects.nonNull(userSearchRequest.getUsername())) {
            predicates.add(criteriaBuilder.like(root.get(User.USERNAME_FIELD), "%" + userSearchRequest.getUsername() + "%"));
        }

        if (Objects.nonNull(userSearchRequest.getDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(User.DELETED_FIELD), userSearchRequest.getDeleted()));
        }

        if (Objects.nonNull(userSearchRequest.getKeyword())) {
            Predicate predicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get(User.EMAIL_FIELD), "%" + userSearchRequest.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(User.USERNAME_FIELD), "%" + userSearchRequest.getKeyword() + "%")
            );

            predicates.add(predicate);
        }

        sort(root, query, criteriaBuilder);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void sort(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if ((Objects.nonNull(userSearchRequest.getUserSortField()))) {
            switch (userSearchRequest.getUserSortField()) {
                case EMAIL -> query.orderBy(criteriaBuilder.asc(root.get(User.EMAIL_FIELD)));
                case USERNAME -> query.orderBy(criteriaBuilder.asc(root.get(User.USERNAME_FIELD)));
                default -> query.orderBy(criteriaBuilder.asc(root.get(User.ID_FIELD)));
            }
        } else {
            query.orderBy(criteriaBuilder.asc(root.get(User.ID_FIELD)));
        }
    }
}
