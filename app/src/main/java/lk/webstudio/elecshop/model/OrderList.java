package lk.webstudio.elecshop.model;

public class OrderList {
    private String productName;
    private int productQty;
    private String productCode;
    private double productPrice;
    private String image_url;

    public OrderList(String productName, int productQty, String productCode, double productPrice, String image_url) {
        this.productName = productName;
        this.productQty = productQty;
        this.productCode = productCode;
        this.productPrice = productPrice;
        this.image_url = image_url;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
