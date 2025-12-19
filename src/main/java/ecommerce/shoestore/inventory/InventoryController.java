package ecommerce.shoestore.inventory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/inventory")
public class InventoryController {

    @GetMapping
    public String showInventoryPage(Model model) {
        // 1. Highlight menu bên trái
        model.addAttribute("activeMenu", "inventory");

        // 2. Đổi tiêu đề trên thanh Topbar
        model.addAttribute("pageTitle", "Quản lý tồn kho");

        // 3. QUAN TRỌNG: Chỉ định nội dung cần nhét vào Layout
        // Cú pháp: "tên_file_html :: tên_fragment"
        // Nghĩa là: Lấy fragment 'main-content' trong file 'admin/inventory.html'
        model.addAttribute("content", "admin/inventory/inventory-main :: main-content");

        // 4. Trả về file LAYOUT CHÍNH (File layout bạn gửi ở trên)
        // Giả sử file layout bạn lưu là: templates/admin/layout.html
        return "admin/layout";
    }
}
