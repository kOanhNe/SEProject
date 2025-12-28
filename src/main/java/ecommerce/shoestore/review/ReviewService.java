package ecommerce.shoestore.review;

import ecommerce.shoestore.auth.user.UserRepository;
import ecommerce.shoestore.review.dto.ReviewRequest;
import ecommerce.shoestore.shoes.Shoes;
import ecommerce.shoestore.shoes.ShoesRepository;
import ecommerce.shoestore.order.OrderItem;
import ecommerce.shoestore.order.OrderItemRepository;
import ecommerce.shoestore.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ShoesRepository shoesRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;


    @Transactional
    public Review createReview(ReviewRequest request, Long userId){

    // 1. Kiểm tra xem OrderItem này đã được review chưa (tránh duplicate)
        if (reviewRepository.existsByOrderItem_OrderItemId(request.getOrderItemId())) {
            throw new RuntimeException("Sản phẩm trong đơn hàng này đã được đánh giá rồi.");
        }

        // 2. Tìm OrderItem
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục đơn hàng này."));

        // 3. Tìm Giày (Shoes)
        Shoes shoe = shoesRepository.findById(request.getShoeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm giày này."));

        // 4. Xây dựng đối tượng Review
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Review review = Review.builder()
                .rate(request.getRate())
                .comment(request.getComment())
                .user(user)
                .shoes(shoe)
                .orderItem(orderItem)
                .build();
        return reviewRepository.save(review);
    }
}