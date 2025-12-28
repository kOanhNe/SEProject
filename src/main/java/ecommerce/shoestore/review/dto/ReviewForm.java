package ecommerce.shoestore.review.dto;

import lombok.Data;

import java.util.List;

// ReviewForm.java
@Data
public class ReviewForm {
    private Long orderId;
    private List<ReviewItemRequest> items; // Chứa productId, rate, comment cho từng món
}
