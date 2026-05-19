package practical.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import practical.post.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByIdAndDeletedFalse(int id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
