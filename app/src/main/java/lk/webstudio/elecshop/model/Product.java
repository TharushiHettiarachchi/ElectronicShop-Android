package lk.webstudio.elecshop.model;

import java.util.Date;

public class Product {

    private String product_id;
    private String product_code;
    private String product_name;
    private int price;
    private int quantity;
    private int status;  // 0 - No cart No wishlist    //1 - Added cart No wishlist  // 2 - No cart Added wishlist  // 3 -  Added wishlist Added cart
    private String user_id;
    private String date_added;
    private String image_url;

    public Product(String product_id, String product_code, String product_name, int price, int quantity, int status, String user_id, String date_added, String image_url) {
        this.product_id = product_id;
        this.product_code = product_code;
        this.product_name = product_name;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.user_id = user_id;
        this.date_added = date_added;
        this.image_url = image_url;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
