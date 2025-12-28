package ecommerce.shoestore.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByShoes_ShoeId(Long shoeId);

    boolean existsByOrderItem_OrderItemId(Long orderItemId);

    boolean existsByOrderItem_OrderId(Long orderId);

    List<Review> findByShoes_ShoeIdOrderByReviewDateDesc(Long shoeId);
    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.orderItem " +
            "WHERE r.shoes.shoeId = :shoeId " +
            "ORDER BY r.reviewDate DESC")
    List<Review> findByShoesIdWithDetails(@Param("shoeId") Long shoeId);
}