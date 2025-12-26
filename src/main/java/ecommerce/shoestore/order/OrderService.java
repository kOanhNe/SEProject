package ecommerce.shoestore.order;

import ecommerce.shoestore.cart.Cart;
import ecommerce.shoestore.cart.CartRepository;
import ecommerce.shoestore.cartitem.CartItem;
import ecommerce.shoestore.payment.Payment;
import ecommerce.shoestore.payment.PaymentRepository;
import ecommerce.shoestore.payment.PaymentTransaction;
import ecommerce.shoestore.payment.PaymentTransactionRepository;
import ecommerce.shoestore.shoesvariant.ShoesVariant;
import ecommerce.shoestore.shoesvariant.ShoesVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ShoesVariantRepository shoesVariantRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderAddressRepository orderAddressRepository;
    
    private static final BigDecimal SHIPPING_FEE = new BigDecimal("30000");
    
    @Transactional
    public Order createOrderFromCart(Long userId, Long addressId, String recipientEmail,
                                     String paymentMethod, String note, Cart cart) {
        
        // Tính subTotal từ cart
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            BigDecimal itemTotal = item.getVariant().getShoes().getBasePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            subTotal = subTotal.add(itemTotal);
        }
        
        // Tính discountAmount (có thể tích hợp voucher sau)
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // Tính totalAmount
        BigDecimal totalAmount = subTotal.add(SHIPPING_FEE).subtract(discountAmount);
        
        // Lấy thông tin địa chỉ để điền recipient fields
        OrderAddress address = orderAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        // Generate order code
        String orderCode = "ORDER" + System.currentTimeMillis();
        
        // Tạo Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderAddressId(addressId);
        order.setRecipientEmail(recipientEmail);
        order.setRecipientName(address.getRecipientName());
        order.setRecipientPhone(address.getRecipientPhone());
        order.setRecipientAddress(
            address.getProvince() + ", " + 
            address.getDistrict() + ", " + 
            address.getCommune() + ", " + 
            address.getStreetDetail()
        );
        order.setSubTotal(subTotal);
        order.setShippingFee(SHIPPING_FEE);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setOrderCode(orderCode);
        order.setNote(note);
        order.setStatus("PENDING");
        
        // Set payment status based on payment method
        if ("VNPAY".equals(paymentMethod)) {
            order.setPaymentStatus("UNPAID");
        } else {
            order.setPaymentStatus("UNPAID");
        }
        
        order = orderRepository.save(order);
        
        // Tạo OrderItems
        for (CartItem item : cart.getItems()) {
            ShoesVariant variant = item.getVariant();
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setShoeId(variant.getShoes().getShoeId());
            orderItem.setQuantity((long) item.getQuantity());
            orderItem.setProductName(variant.getShoes().getName());
            orderItem.setVariantInfo("Size: " + variant.getSize() + ", Color: " + variant.getColor());
            orderItem.setUnitPrice(variant.getShoes().getBasePrice());
            orderItem.setShopDiscount(BigDecimal.ZERO);
            orderItem.setItemTotal(variant.getShoes().getBasePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemRepository.save(orderItem);
        }
        
        // Tạo Payment và PaymentTransaction cho VNPay
        if ("VNPAY".equals(paymentMethod)) {
            // Tạo Payment record
            Payment payment = new Payment();
            payment.setOrderId(order.getOrderId());
            payment.setAmount(totalAmount);
            payment.setProvider("VNPAY");
            payment.setCurrency("VND");
            payment.setStatus("PENDING");
            paymentRepository.save(payment);
            
            // Tạo PaymentTransaction record
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setOrderId(order.getOrderId());
            transaction.setAmount(totalAmount);
            transaction.setVnpTxnRef(String.valueOf(order.getOrderId()));
            transaction.setPaymentMethod("VNPAY");
            transaction.setStatus("PENDING");
            paymentTransactionRepository.save(transaction);
            
            System.out.println("Created Payment and PaymentTransaction for VNPay order: " + order.getOrderId());
        }
        
        // Xóa cart
        cartRepository.delete(cart);
        
        return order;
    }
    
    @Transactional
    public Order createOrderBuyNow(Long userId, Long addressId, String recipientEmail,
                                   String paymentMethod, String note,
                                   Long variantId, Integer quantity) {
        
        ShoesVariant variant = shoesVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        
        // Tính subTotal
        BigDecimal subTotal = variant.getShoes().getBasePrice()
                .multiply(BigDecimal.valueOf(quantity));
        
        // Tính discountAmount
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        // Tính totalAmount
        BigDecimal totalAmount = subTotal.add(SHIPPING_FEE).subtract(discountAmount);
        
        // Lấy thông tin địa chỉ để điền recipient fields
        OrderAddress address = orderAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
        
        // Generate order code
        String orderCode = "ORDER" + System.currentTimeMillis();
        
        // Tạo Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderAddressId(addressId);
        order.setRecipientEmail(recipientEmail);
        order.setRecipientName(address.getRecipientName());
        order.setRecipientPhone(address.getRecipientPhone());
        order.setRecipientAddress(
            address.getProvince() + ", " + 
            address.getDistrict() + ", " + 
            address.getCommune() + ", " + 
            address.getStreetDetail()
        );
        order.setSubTotal(subTotal);
        order.setShippingFee(SHIPPING_FEE);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(paymentMethod);
        order.setOrderCode(orderCode);
        order.setNote(note);
        order.setStatus("PENDING");
        
        // Set payment status based on payment method
        if ("VNPAY".equals(paymentMethod)) {
            order.setPaymentStatus("UNPAID");
        } else {
            order.setPaymentStatus("UNPAID");
        }
        
        order = orderRepository.save(order);
        
        // Tạo OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setShoeId(variant.getShoes().getShoeId());
        orderItem.setQuantity((long) quantity);
        orderItem.setProductName(variant.getShoes().getName());
        orderItem.setVariantInfo("Size: " + variant.getSize() + ", Color: " + variant.getColor());
        orderItem.setUnitPrice(variant.getShoes().getBasePrice());
        orderItem.setShopDiscount(BigDecimal.ZERO);
        orderItem.setItemTotal(subTotal);
        orderItemRepository.save(orderItem);
        
        // Tạo Payment và PaymentTransaction cho VNPay
        if ("VNPAY".equals(paymentMethod)) {
            // Tạo Payment record
            Payment payment = new Payment();
            payment.setOrderId(order.getOrderId());
            payment.setAmount(totalAmount);
            payment.setProvider("VNPAY");
            payment.setCurrency("VND");
            payment.setStatus("PENDING");
            paymentRepository.save(payment);
            
            // Tạo PaymentTransaction record
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setOrderId(order.getOrderId());
            transaction.setAmount(totalAmount);
            transaction.setVnpTxnRef(String.valueOf(order.getOrderId()));
            transaction.setPaymentMethod("VNPAY");
            transaction.setStatus("PENDING");
            paymentTransactionRepository.save(transaction);
            
            System.out.println("Created Payment and PaymentTransaction for VNPay order: " + order.getOrderId());
        }
        
        return order;
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}
