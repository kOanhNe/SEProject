package ecommerce.shoestore.revenue;

import ecommerce.shoestore.order.Order;
import ecommerce.shoestore.order.OrderHistoryRepository;
import ecommerce.shoestore.order.OrderStatus;
import ecommerce.shoestore.revenue.dto.RevenueReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueService {
    
    private final OrderHistoryRepository orderRepository;
    
    public RevenueReportDto generateRevenueReport(LocalDate startDate, LocalDate endDate, String reportType) {
        List<Order> completedOrders = getCompletedOrdersInDateRange(startDate, endDate);
        
        BigDecimal totalRevenue = calculateTotalRevenue(completedOrders);
        Long totalCompletedOrders = (long) completedOrders.size();
        BigDecimal averageOrderValue = totalCompletedOrders > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalCompletedOrders), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        return RevenueReportDto.builder()
            .startDate(startDate)
            .endDate(endDate)
            .reportType(reportType)
            .totalRevenue(totalRevenue)
            .totalCompletedOrders(totalCompletedOrders)
            .averageOrderValue(averageOrderValue)
            .dailyBreakdown(generateDailyBreakdown(completedOrders, startDate, endDate))
            .orderDetails(generateOrderDetails(completedOrders))
            .build();
    }
    
    private List<Order> getCompletedOrdersInDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return orderRepository.findAll().stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
            .filter(order -> {
                LocalDateTime createAt = order.getCreateAt();
                return createAt != null && 
                       !createAt.isBefore(startDateTime) && 
                       createAt.isBefore(endDateTime);
            })
            .collect(Collectors.toList());
    }
    
    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
            .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private List<RevenueReportDto.DailyRevenueDto> generateDailyBreakdown(List<Order> orders, 
                                                                          LocalDate startDate, 
                                                                          LocalDate endDate) {
        Map<LocalDate, List<Order>> ordersByDate = orders.stream()
            .collect(Collectors.groupingBy(order -> order.getCreateAt().toLocalDate()));
        
        return startDate.datesUntil(endDate.plusDays(1))
            .map(date -> {
                List<Order> dayOrders = ordersByDate.getOrDefault(date, List.of());
                BigDecimal dayRevenue = calculateTotalRevenue(dayOrders);
                
                return RevenueReportDto.DailyRevenueDto.builder()
                    .date(date)
                    .revenue(dayRevenue)
                    .orderCount((long) dayOrders.size())
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    private List<RevenueReportDto.RevenueOrderDto> generateOrderDetails(List<Order> orders) {
        return orders.stream()
            .map(order -> {
                return RevenueReportDto.RevenueOrderDto.builder()
                    .orderId(order.getOrderId())
                    .customerName(order.getRecipientName() != null ? order.getRecipientName() : "Khách hàng")
                    .customerEmail(order.getRecipientEmail() != null ? order.getRecipientEmail() : "N/A")
                    .orderDate(order.getCreateAt().toLocalDate())
                    .totalAmount(order.getTotalAmount())
                    .status("Hoàn thành")
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    public RevenueReportDto getQuickStats() {
        LocalDate today = LocalDate.now();
        LocalDate thisMonthStart = today.withDayOfMonth(1);
        
        return generateRevenueReport(thisMonthStart, today, "MONTHLY");
    }
}