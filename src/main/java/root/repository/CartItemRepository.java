package root.repository;

import root.models.CartItem;
import root.models.Product;
import root.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByProduct(Product product);
    List<CartItem> findByUser(User user);

}
