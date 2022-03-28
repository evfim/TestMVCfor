package root.repository;

import root.models.Product;
import root.models.Rating;
import root.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByUser(User user);
    List<Rating> findByProduct(Product product);
}
