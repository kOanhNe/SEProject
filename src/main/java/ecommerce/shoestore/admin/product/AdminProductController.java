package ecommerce.shoestore.admin.product;

import ecommerce.shoestore.admin.product.dto.AdminShoesDetailDto;
import ecommerce.shoestore.admin.product.dto.AdminShoesListItemDto;
import ecommerce.shoestore.admin.product.dto.CreateShoesRequest;
import ecommerce.shoestore.admin.product.dto.UpdateShoesRequest;
import ecommerce.shoestore.category.Category;
import ecommerce.shoestore.category.CategoryRepository;
import ecommerce.shoestore.shoes.ShoesType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;

    /**
     * Hiển thị danh sách sản phẩm + phân trang + tìm kiếm + lọc
     */
    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            Model model
    ) {
        log.info(
                "Admin xem danh sách sản phẩm - trang={}, kích thước={}, từ khóa={}, danh mục={}, thương hiệu={}, trạng thái={}",
                page, size, keyword, categoryId, brand, status
        );

        Page<AdminShoesListItemDto> productPage =
                adminProductService.getAdminProductList(page, size, keyword, categoryId, brand, status);

        // AF2: trang vượt quá tổng số trang
        if (page > productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            log.warn("Trang yêu cầu vượt quá tổng số trang, chuyển về trang cuối");
            return "redirect:/admin/products?page=" + productPage.getTotalPages();
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brand", brand);
        model.addAttribute("status", status);

        // Load categories cho dropdown tìm kiếm
        List<Category> categories = categoryRepository.findAllByOrderByNameAsc();
        model.addAttribute("categories", categories);

        return "admin/product/list";
    }

    /**
     * Mở form thêm sản phẩm
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Admin mở form thêm sản phẩm mới");

        // Load categories cho dropdown
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        // Load ShoesType enum
        model.addAttribute("shoesTypes", ShoesType.values());
        
        model.addAttribute("request", new CreateShoesRequest());

        return "admin/product/create";
    }

    /**
     * UC 2.B
     * Xử lý submit thêm sản phẩm
     */
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("request") CreateShoesRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Admin gửi yêu cầu thêm sản phẩm mới: {}", request.getName());

        // EF2: Validate errors
        if (bindingResult.hasErrors()) {
            log.warn("Lỗi validation khi thêm sản phẩm: {}", bindingResult.getAllErrors());
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            
            return "admin/product/create";
        }

        try {
            Long shoeId = adminProductService.createShoes(request);
            log.info("Tạo sản phẩm thành công - ID: {}", shoeId);
            
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công");
            return "redirect:/admin/products";
            
        } catch (IllegalArgumentException e) {
            log.error("Lỗi validation nghiệp vụ: {}", e.getMessage());
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", e.getMessage());
            
            return "admin/product/create";
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo sản phẩm", e);
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", "Có lỗi xảy ra khi thêm sản phẩm. Vui lòng thử lại.");
            
            return "admin/product/create";
        }
    }

    /**
     * UC 2.C
     * Mở form chỉnh sửa sản phẩm
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model
    ) {
        log.info("Admin mở form chỉnh sửa sản phẩm - mã sản phẩm={}", id);

        try {
            AdminShoesDetailDto shoesDetail = adminProductService.getAdminShoesDetail(id);
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("shoes", shoesDetail);
            
            return "admin/product/edit";
            
        } catch (Exception e) {
            log.error("Lỗi khi load thông tin sản phẩm {}", id, e);
            model.addAttribute("error", "Không tìm thấy sản phẩm");
            return "redirect:/admin/products";
        }
    }

    /**
     * UC 2.C
     * Xử lý submit chỉnh sửa sản phẩm
     */
    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("request") UpdateShoesRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Admin gửi yêu cầu cập nhật sản phẩm - mã sản phẩm={}", id);

        // EF2: Validate errors
        if (bindingResult.hasErrors()) {
            log.warn("Lỗi validation khi cập nhật sản phẩm {}: {}", id, bindingResult.getAllErrors());
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            
            return "admin/product/edit";
        }

        try {
            adminProductService.updateShoes(id, request);
            log.info("Cập nhật sản phẩm thành công - ID: {}", id);
            
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công");
            return "redirect:/admin/products";
            
        } catch (IllegalArgumentException e) {
            log.error("Lỗi validation nghiệp vụ: {}", e.getMessage());
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", e.getMessage());
            
            return "admin/product/edit";
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật sản phẩm {}", id, e);
            
            List<Category> categories = categoryRepository.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("shoesTypes", ShoesType.values());
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật sản phẩm. Vui lòng thử lại.");
            
            return "admin/product/edit";
        }
    }

    /**
     * UC 2.D
     * Tạm ngừng bán sản phẩm
     */
    @PostMapping("/{id}/disable")
    public String disableProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Admin tạm ngừng bán sản phẩm - mã sản phẩm={}", id);

        try {
            adminProductService.disableShoes(id);
            log.info("Tạm ngừng bán sản phẩm thành công - ID: {}", id);
            
            redirectAttributes.addFlashAttribute("success", "Đã tạm ngừng bán sản phẩm");
            
        } catch (Exception e) {
            log.error("Lỗi khi tạm ngừng bán sản phẩm {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Không thể tạm ngừng bán sản phẩm. Vui lòng thử lại.");
        }

        return "redirect:/admin/products";
    }

    /**
     * UC 2.E
     * Xóa mềm sản phẩm
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Admin xóa mềm sản phẩm - mã sản phẩm={}", id);

        try {
            adminProductService.deleteShoes(id);
            log.info("Xóa sản phẩm thành công - ID: {}", id);
            
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi hệ thống. Khách hàng sẽ không thể xem sản phẩm này nữa.");
            
        } catch (Exception e) {
            log.error("Lỗi khi xóa sản phẩm {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm. Vui lòng thử lại.");
        }

        return "redirect:/admin/products";
    }
}
