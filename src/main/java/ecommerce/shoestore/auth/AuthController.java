package ecommerce.shoestore.auth;

import ecommerce.shoestore.auth.dto.*;
import ecommerce.shoestore.auth.user.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ================= REGISTER =================

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            return "auth/register";
        }

        try {
            authService.register(request);

            VerifyEmailRequest verifyReq = new VerifyEmailRequest();
            verifyReq.setEmail(request.getEmail());

            model.addAttribute("verifyRequest", verifyReq);
            model.addAttribute(
                    "message",
                    "Đăng ký thành công! Mã xác thực đã gửi đến email: " + request.getEmail()
            );

            return "auth/verify-email";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // ================= VERIFY EMAIL =================

    @GetMapping("/verify-email")
    public String showVerifyForm(Model model) {
        model.addAttribute("verifyRequest", new VerifyEmailRequest());
        return "auth/verify-email";
    }

    @PostMapping("/verify-email")
    public String processVerify(@ModelAttribute VerifyEmailRequest request, Model model) {
        try {
            authService.verifyEmail(request);

            model.addAttribute("loginRequest", new LoginRequest());
            model.addAttribute("message", "Kích hoạt tài khoản thành công! Vui lòng đăng nhập.");

            return "auth/login";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("verifyRequest", request);
            return "auth/verify-email";
        }
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest request,
            HttpSession session,
            Model model
    ) {
        try {
            User user = authService.login(request);

            // ===== SESSION (GIỮ NGUYÊN CODE CŨ) =====
            session.setAttribute("USER_ID", user.getUserId());
            session.setAttribute("FULLNAME", user.getFullname());
            session.setAttribute("ROLE", user.getAccount().getRole());
            session.setAttribute("AVATAR", user.getAvatar());

            // ===== SPRING SECURITY AUTH (THÊM) =====
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            AuthorityUtils.createAuthorityList(
                                    "ROLE_" + user.getAccount().getRole().name()
                            )
                    );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(token);
            SecurityContextHolder.setContext(context);

            // 🔥 CỰC QUAN TRỌNG – LƯU VÀO SESSION
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            // ===== REDIRECT ADMIN (THÊM) =====
            if (user.getAccount().getRole().name().equals("ADMIN")) {
                return "redirect:/admin";
            }

            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // ================= FORGOT PASSWORD =================

    @GetMapping("/forgot-password")
    public String showForgotForm(Model model) {
        model.addAttribute("request", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@ModelAttribute ForgotPasswordRequest request, Model model) {
        try {
            authService.forgotPassword(request.getEmail());

            ResetPasswordRequest resetReq = new ResetPasswordRequest();
            resetReq.setEmail(request.getEmail());

            model.addAttribute("resetRequest", resetReq);
            model.addAttribute("message", "Mã reset mật khẩu đã được gửi tới email.");

            return "auth/reset-password";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    // ================= RESET PASSWORD =================

    @GetMapping("/reset-password")
    public String showResetForm(Model model) {
        model.addAttribute("resetRequest", new ResetPasswordRequest());
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processReset(
            @ModelAttribute("resetRequest") ResetPasswordRequest request,
            Model model
    ) {
        if (request.getNewPassword() == null || request.getConfirmPassword() == null) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ mật khẩu!");
            return "auth/reset-password";
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            return "auth/reset-password";
        }

        try {
            authService.resetPassword(request);

            model.addAttribute("message", "Đổi mật khẩu thành công. Hãy đăng nhập lại.");
            model.addAttribute("loginRequest", new LoginRequest());

            return "auth/login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }
    }

    // ================= LOGOUT =================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/auth/login?logout";
    }
}
