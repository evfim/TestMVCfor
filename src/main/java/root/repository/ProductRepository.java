package root.repository;

import root.models.Manufacturer;
import root.models.Product;
import root.models.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
    List<Product> findByProductType(ProductType productType);
    List<Product> findByManufacturer(Manufacturer manufacturer);
}
