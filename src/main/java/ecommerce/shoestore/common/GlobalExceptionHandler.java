package ecommerce.shoestore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception handler cho toàn bộ application
 * Xử lý các exception throw từ Controller
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý NotFoundException → Spring Boot tự render file templates/error/404.html
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex, Model model) {
        log.error("NotFoundException: {}", ex.getMessage());

        model.addAttribute("message", ex.getMessage());
        model.addAttribute("error", "404");

        // return "error/404" thì trực tiếp vào file 404.html
        return "error/404";
    }

    /**
     * Xử lý lỗi tổng quát → render templates/error/500.html
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Unexpected error occurred", ex);

        model.addAttribute("message", "Đã xảy ra lỗi. Vui lòng thử lại sau.");
        model.addAttribute("error", "500");

        return "error/500";
    }
}