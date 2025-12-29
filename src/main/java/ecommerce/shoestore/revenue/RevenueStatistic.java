package ecommerce.shoestore.revenue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"revenuestatistic\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatistic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"statId\"")
    private Long statId;
    
    @Column(name = "\"reportType\"")
    private String reportType;
    
    @Column(name = "\"startDate\"")
    private LocalDate startDate;
    
    @Column(name = "\"endDate\"") 
    private LocalDate endDate;
    
    @Column(name = "\"totalRevenue\"")
    private Double totalRevenue;
    
    @Column(name = "\"totalCompletedOrders\"")
    private Long totalCompletedOrders;
    
    @Column(name = "\"generatedAt\"")
    private LocalDateTime generatedAt;
}