package ecommerce.shoestore.promotion;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller cho customer xem và sử dụng vouchers
 */
@Controller
@RequestMapping("/vouchers")
@RequiredArgsConstructor
@Slf4j
public class CustomerPromotionController {

    private final CustomerPromotionService customerPromotionService;

    /**
     * Hiển thị trang danh sách vouchers khả dụng cho customer
     */
    @GetMapping
    public String listVouchers(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("USER_ID");
        
        // Nếu chưa login, vẫn hiển thị vouchers nhưng không check usage
        BigDecimal orderValue = new BigDecimal("0"); // Default order value
        
        List<Voucher> availableVouchers;
        if (userId != null) {
            availableVouchers = customerPromotionService.getAvailableVouchers(userId, orderValue);
        } else {
            // Guest users - chỉ lấy vouchers enabled và trong thời gian hiệu lực
            availableVouchers = customerPromotionService.getAvailableVouchers(null, orderValue);
        }
        
        model.addAttribute("vouchers", availableVouchers);
        model.addAttribute("pageTitle", "Mã giảm giá");
        
        return "user/voucher-list";
    }
}
