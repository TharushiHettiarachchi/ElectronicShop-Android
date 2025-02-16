package lk.webstudio.elecshop.model;

public class OrdersProducts {

    private String product_name;
    private String product_code;
    private Double price;
    private int quantity;
    private String image_url;

    public OrdersProducts(String product_name, String product_code, Double price, int quantity, String image_url) {
        this.product_name = product_name;
        this.product_code = product_code;
        this.price = price;
        this.quantity = quantity;
        this.image_url = image_url;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
