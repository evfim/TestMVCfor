package root.repository;

import root.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItem2Repository extends JpaRepository<CartItem2, Long> {
    CartItem2 findByProduct(Product product);
    List<CartItem2> findByUser(User user);
    List<CartItem2> findByOrders(Orders orders);

}
