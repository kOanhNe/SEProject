package ecommerce.shoestore.cartitem;
//import ecommerce.shoestore.shoes.Shoes;

public class CartItem {

    private long id;
    //private Shoes item;
    private int quantity;
    private String description;

    //CONSTRUCTORS
    public CartItem(long id, /*Shoes item,*/ int quantity, String description) {
        this.id = id;
        //this.item = item;
        this.quantity = quantity;
        this.description = description;
    }

    public CartItem() {}

    //GETTERS AND SETTERS
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    //public Shoes getItem() { return item; }
    //public void setItem(Shoes item) { this.item = item; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
