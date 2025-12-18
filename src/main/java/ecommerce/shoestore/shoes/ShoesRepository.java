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
       CUSTOMER – RELATED PRODUCT
       ========================= */
    // TODO: Uncomment khi DB có column deleted
    /*
    @Query("SELECT DISTINCT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category " +
           "LEFT JOIN FETCH s.images " +
           "WHERE s.category.categoryId = :categoryId " +
           "AND s.shoeId <> :excludeShoeId " +
           "AND s.deleted = false")
    Page<Shoes> findRelatedProducts(
            @Param("categoryId") Long categoryId,
            @Param("excludeShoeId") Long excludeShoeId,
            Pageable pageable
    );
    */

    /* =========================
       ADMIN – MANAGE PRODUCT
       ========================= */
    // TẠM THỜI - Tìm kiếm cơ bản (không dùng deleted/status)
    @Query("SELECT DISTINCT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category c " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(s.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR c.categoryId = :categoryId) " +
           "AND (:brand IS NULL OR :brand = '' OR s.brand = :brand)")
    Page<Shoes> searchForAdminBasic(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            Pageable pageable
    );

    // TODO: Uncomment khi DB có columns deleted, status
    /*
    @Query("""
        SELECT s
        FROM Shoes s
        WHERE s.deleted = false
          AND (:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR s.category.categoryId = :categoryId)
          AND (:brand IS NULL OR s.brand = :brand)
          AND (:status IS NULL OR s.status = :status)
    """)
    Page<Shoes> searchForAdmin(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            @Param("status") String status,
            Pageable pageable
    );
    */

    // TODO: Uncomment khi DB có column deleted
    /*
    @Query("SELECT s FROM Shoes s " +
           "LEFT JOIN FETCH s.category " +
           "LEFT JOIN FETCH s.images " +
           "LEFT JOIN FETCH s.variants " +
           "WHERE s.shoeId = :shoeId AND s.deleted = false")
    Optional<Shoes> findByIdForAdmin(@Param("shoeId") Long shoeId);
    */
}
