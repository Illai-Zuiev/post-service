package practical.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practical.post.model.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
