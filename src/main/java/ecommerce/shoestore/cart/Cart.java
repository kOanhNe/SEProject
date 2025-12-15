package ecommerce.shoestore.cart;
<<<<<<< HEAD
//import ecommerce.shoestore.customer.Customer;
import ecommerce.shoestore.cartitem.CartItem;
import jakarta.annotation.Generated;
import jakarta.persistence.*;


@Entity
public class Cart {

    @Id @GeneratedValue
    private long id;

    //@OneToOne
    //@JoinColumn(name = "customerId")
    //private Customer customer;

    //@OneToMany(mappedBy = "cart", cascade = CassadeType.ALL, orphanRemoval = true)
    //private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    //public Cart(Customer customer){
        //this.customer = customer;
        //}

    public void addItem(CartItem item){
        //this.items.add(item);
        //item.setCart(this);
    }
    
    //GETTERS AND SETTERS
    public long getId() { return id; }

    //public Customer getCustomer() { return customer; }
    //public void setCustomer(Customer customer) { this.customer = customer; }

   // public List<CartItem> getItems() { return items; }
    //public void setItems(List<CartItem> items) { this.items = items; }
=======

import ecommerce.shoestore.auth.user.User;
import jakarta.persistence.*;
import lombok.*;
import ecommerce.shoestore.cartitem.CartItem;
import ecommerce.shoestore.shoes.Shoes;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@NoArgsConstructor 
@Getter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User customer;

    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )

    private List<CartItem> items = new ArrayList<>();

    public Cart(User customer){
        this.customer = customer;
    }

    public void addItem(Shoes shoes, int quantity){
        for(CartItem item : items){
            if (item.getShoes().getShoeId().equals(shoes.getShoeId())){
                item.increaseQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(Long shoeId){
        items.removeIf(item -> item.getShoes().getShoeId().equals(shoeId));
    }
>>>>>>> a9097a0 (Update Cart and CartItem object)
}
