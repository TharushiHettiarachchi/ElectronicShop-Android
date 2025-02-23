package lk.webstudio.elecshop.model;

import java.util.Date;

public class UserOrders {
    private String productId;
    private String productName;
    private String productCode;
    private String imageUrl;
    private int qty;
    private Double price;
    private String customerID;
    private String customerFname;
    private String customerLname;
    private String customerEmail;
    private String customerMobile;
    private String orderId;
    private Date orderDate;
    private int orderStatus;

    public UserOrders(String productId, String productName, String productCode, String imageUrl, int qty, Double price, String customerID, String customerFname, String customerLname, String customerEmail, String customerMobile, String orderId, Date orderDate, int orderStatus) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.imageUrl = imageUrl;
        this.qty = qty;
        this.price = price;
        this.customerID = customerID;
        this.customerFname = customerFname;
        this.customerLname = customerLname;
        this.customerEmail = customerEmail;
        this.customerMobile = customerMobile;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerFname() {
        return customerFname;
    }

    public void setCustomerFname(String customerFname) {
        this.customerFname = customerFname;
    }

    public String getCustomerLname() {
        return customerLname;
    }

    public void setCustomerLname(String customerLname) {
        this.customerLname = customerLname;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
