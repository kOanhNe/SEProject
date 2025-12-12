package ecommerce.shoestore.cartitem;

import ecommerce.shoestore.cart.Cart;
import ecommerce.shoestore.shoes.Shoes;
import jakarta.persistence.*;

@Entity
public class CartItem {


    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "shoeId")
    private Shoes item;

    private int quantity;
    private String description;

    //CONSTRUCTORS
    public CartItem(Cart cart, Shoes item, int quantity, String description) {
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
        this.description = description;
    }

    public CartItem() {}

    //GETTERS AND SETTERS
    public Long getId() { return id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Shoes getItem() { return item; }
    public void setItem(Shoes item) { this.item = item; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
