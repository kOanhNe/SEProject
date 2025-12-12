package ecommerce.shoestore.cart;
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
}
