package practical.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practical.post.model.entity.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    boolean existsByTitle(String title);

    Optional<Post> findByIdAndDeletedFalse(int id);
}
