package ecommerce.shoestore.review.dto;

import lombok.Data;
@Data
public class ReviewItemRequest {
    private Long orderItemId;
    private Long shoeId;
    private int rate;
    private String comment;
}