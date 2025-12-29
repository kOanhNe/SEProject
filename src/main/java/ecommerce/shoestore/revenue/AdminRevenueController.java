package ecommerce.shoestore.revenue;

import ecommerce.shoestore.revenue.dto.RevenueReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin/revenue")
@RequiredArgsConstructor
public class AdminRevenueController {
    
    private final RevenueService revenueService;
    
    @GetMapping
    public String showRevenuePage(Model model) {
        // Thiết lập bộ lọc mặc định theo use case: tháng hiện tại
        LocalDate today = LocalDate.now();
        LocalDate thisMonthStart = today.withDayOfMonth(1);
        
        model.addAttribute("startDate", thisMonthStart.toString());
        model.addAttribute("endDate", today.toString());
        model.addAttribute("reportType", "monthly");
        
        // Load báo cáo doanh thu mặc định tháng hiện tại
        RevenueReportDto quickStats = revenueService.getQuickStats();
        model.addAttribute("report", quickStats);
        
        return "admin/revenue/dashboard";
    }
    
    @GetMapping("/report")
    @ResponseBody
    public RevenueReportDto generateReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "daily") String reportType) {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        return revenueService.generateRevenueReport(start, end, reportType);
    }
    
    @GetMapping("/export")
    public void exportToCSV(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletResponse response) throws IOException {
        
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        RevenueReportDto report = revenueService.generateRevenueReport(start, end, "daily");
        
        // Tạo CSV trực tiếp từ DTO
        StringBuilder csv = new StringBuilder();
        csv.append("Mã đơn hàng,Khách hàng,Email,Ngày đặt,Tổng tiền,Trạng thái\n");
        
        for (RevenueReportDto.RevenueOrderDto order : report.getOrderDetails()) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                order.getOrderId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getOrderDate().toString(),
                order.getTotalAmount(),
                order.getStatus()));
        }
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"revenue_report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv\"");
        
        response.getWriter().write(csv.toString());
    }
}