package ecommerce.shoestore.auth.user;

import ecommerce.shoestore.auth.user.dto.ChangePasswordRequest;
import ecommerce.shoestore.auth.user.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        System.out.println("DEBUG PROFILE:");
        System.out.println(" - Session ID: " + session.getId());

        Long userId = (Long) session.getAttribute("USER_ID");
        String email = (String) session.getAttribute("EMAIL");

        System.out.println(" - USER_ID trong Session: " + userId);
        System.out.println(" - EMAIL trong Session: " + email);

        if (userId == null || email == null) {
            System.out.println(">>> THẤT BẠI: Session thiếu dữ liệu -> Redirect về Login");
            return "redirect:/auth/login";
        }

        try {
            UpdateProfileRequest profileData = userService.getProfileForDisplay(email);

            model.addAttribute("profile", profileData);
            return "user/profile";
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute UpdateProfileRequest request,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("EMAIL");
        if (email == null) return "redirect:/auth/login";

        try {
            userService.updateProfile(email, request);

            session.setAttribute("FULLNAME", request.getFullname());

            User updatedUser = userService.getCurrentUser(email);
            session.setAttribute("AVATAR", updatedUser.getAvatar());

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordRequest request,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("USER_NAME"); // AuthController lưu email làm định danh
        if (username == null) return "redirect:/auth/login";

        try {
            userService.changePassword( username, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("activeTab", "password");

        return "redirect:/user/profile";
    }
}