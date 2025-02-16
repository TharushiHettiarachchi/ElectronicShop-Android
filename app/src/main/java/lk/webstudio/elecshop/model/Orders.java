package lk.webstudio.elecshop.model;

import java.util.ArrayList;
import java.util.Date;

public class Orders {

    private String orderId;
    private String customerID;
    private java.util.Date Date;
    private Double amount;
    private int status;
    private ArrayList<OrdersProducts> products;

    public Orders(String orderId, String customerID, java.util.Date date, Double amount, int status, ArrayList<OrdersProducts> products) {
        this.orderId = orderId;
        this.customerID = customerID;
        Date = date;
        this.amount = amount;
        this.status = status;
        this.products = products;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<OrdersProducts> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<OrdersProducts> products) {
        this.products = products;
    }
}
