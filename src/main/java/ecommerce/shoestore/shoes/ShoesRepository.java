package ecommerce.shoestore.shoes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoesRepository extends JpaRepository<Shoes, Long> {

    /* =========================
       CUSTOMER – LIST PRODUCT
       ========================= */
    @Query("SELECT DISTINCT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category " +
           "LEFT JOIN FETCH s.images")
    Page<Shoes> findAll(Pageable pageable);

    /* =========================
       CUSTOMER – PRODUCT DETAIL
       ========================= */
    @Query("SELECT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category " +
           "LEFT JOIN FETCH s.images " +
           "LEFT JOIN FETCH s.variants " +
           "WHERE s.shoeId = :shoeId")
    Optional<Shoes> findByIdWithDetails(@Param("shoeId") Long shoeId);

    /* =========================
       ADMIN – MANAGE PRODUCT
       ========================= */
    @Query("SELECT DISTINCT s FROM Shoes s " +
           "LEFT JOIN s.category c " +
           "WHERE (:keyword IS NULL OR :keyword = '' " +
           "   OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(s.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR c.categoryId = :categoryId) " +
           "AND (:brand IS NULL OR :brand = '' OR LOWER(s.brand) = LOWER(:brand)) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<Shoes> searchForAdminBasic(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            @Param("status") Boolean status,
            Pageable pageable
    );

    @Query("SELECT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category c " +
           "LEFT JOIN FETCH s.images " +
           "LEFT JOIN FETCH s.variants " +
           "WHERE s.shoeId = :shoeId")
    Optional<Shoes> findByIdForAdmin(@Param("shoeId") Long shoeId);
}
