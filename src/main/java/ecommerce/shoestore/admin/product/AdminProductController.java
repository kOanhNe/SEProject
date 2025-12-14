package ecommerce.shoestore.admin.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("content", "admin/product/list :: content");
        return "admin/layout";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("content", "admin/product/create :: content");
        return "admin/layout";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("content", "admin/product/edit :: content");
        return "admin/layout";
    }
}
