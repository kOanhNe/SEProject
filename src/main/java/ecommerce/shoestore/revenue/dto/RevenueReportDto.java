package ecommerce.shoestore.revenue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String reportType;
    private BigDecimal totalRevenue;
    private Long totalCompletedOrders;
    private BigDecimal averageOrderValue;
    private List<DailyRevenueDto> dailyBreakdown;
    private List<RevenueOrderDto> orderDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenueDto {
        private LocalDate date;
        private BigDecimal revenue;
        private Long orderCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueOrderDto {
        private Long orderId;
        private String customerName;
        private String customerEmail;
        private LocalDate orderDate;
        private BigDecimal totalAmount;
        private String status;
    }
}