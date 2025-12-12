package ecommerce.shoestore.cart;
//import ecommerce.shoestore.customer.Customer;
import ecommerce.shoestore.cartitem.CartItem;

public class Cart {

    private long id;
    //private Customer customer;
    //private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(long id /*Customer customer*/){
        this.id = id;
        //this.customer = customer;
        //this.items = new ArrayList<>();
        }

    public void addItem(CartItem item){
        //this.items.add(item);
    }
    
    //GETTERS AND SETTERS
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    //public Customer getCustomer() { return customer; }
    //public void setCustomer(Customer customer) { this.customer = customer; }

   // public List<CartItem> getItems() { return items; }
    //public void setItems(List<CartItem> items) { this.items = items; }
}
