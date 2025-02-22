package lk.webstudio.elecshop.model;

public class CartList {

    private String product_id;
    private String product_code;
    private String product_name;
    private int price;
    private int quantity_available;
    private int quantity_ordered;
    private String customer_id;
    private String image_url;
    private String cartID;

    public CartList(String product_id, String product_code, String product_name, int price, int quantity_available, int quantity_ordered, String customer_id, String image_url, String cartID) {
        this.product_id = product_id;
        this.product_code = product_code;
        this.product_name = product_name;
        this.price = price;
        this.quantity_available = quantity_available;
        this.quantity_ordered = quantity_ordered;
        this.customer_id = customer_id;
        this.image_url = image_url;
        this.cartID = cartID;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity_available() {
        return quantity_available;
    }

    public void setQuantity_available(int quantity_available) {
        this.quantity_available = quantity_available;
    }

    public int getQuantity_ordered() {
        return quantity_ordered;
    }

    public void setQuantity_ordered(int quantity_ordered) {
        this.quantity_ordered = quantity_ordered;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }
}
