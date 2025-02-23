package lk.webstudio.elecshop.navigations;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.Orders;
import lk.webstudio.elecshop.model.OrdersProducts;
import lk.webstudio.elecshop.model.Product;


public class PurchasedItemFragment extends Fragment {
    ArrayList<Orders> orderlist = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_purchased_item, container, false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("orders")
                .whereEqualTo("customer_id", MainActivity.userLogId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (documentSnapshot.exists()) {
                            try {
                                // Extracting data
                                String orderId = documentSnapshot.getId(); // Get the document ID
                                String customerId = documentSnapshot.getString("customer_id");
                                long amount = documentSnapshot.getLong("amount");
                                int status = Integer.parseInt(String.valueOf(documentSnapshot.getLong("status")));
                                Date dateOrdered = documentSnapshot.getDate("date_ordered");
                                List<Map<String, Object>> products = (List<Map<String, Object>>) documentSnapshot.get("products");

                                // Logging the data
                                Log.d("ElecLog", "Order ID: " + orderId);
                                Log.d("ElecLog", "Customer ID: " + customerId);
                                Log.d("ElecLog", "Amount: " + amount);
                                Log.d("ElecLog", "Date Ordered: " + dateOrdered);

                                ArrayList<OrdersProducts> orderProductslist = new ArrayList<>();
                                if (products != null) {
                                    for (Map<String, Object> product : products) {
                                        String productCode = (String) product.get("productCode");
                                        String productName = (String) product.get("productName");
                                        String imageURL = (String) product.get("image_url");
                                        double productPrice = Double.parseDouble(String.valueOf(product.get("productPrice")));
                                        int productQty = Integer.parseInt(String.valueOf(product.get("productQty")));

                                        orderProductslist.add(new OrdersProducts(productCode, productName, productPrice, productQty, imageURL));

                                        Log.d("ElecLog", "Product Code: " + productCode);
                                        Log.d("ElecLog", "Product Name: " + productName);
                                        Log.d("ElecLog", "Product Price: " + productPrice);
                                        Log.d("ElecLog", "Product Quantity: " + productQty);
                                        Log.i("ElecLog", "--------------------------------");
                                    }
                                }
                                orderlist.add(new Orders(orderId, customerId, dateOrdered, Double.parseDouble(String.valueOf(amount)), status, orderProductslist));


                            } catch (Exception e) {
                                Log.i("ElecLog", String.valueOf(e));
                            }
                        }
                    }
                    RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerView5);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    recyclerView1.setLayoutManager(layoutManager);
                    OrderAdapter orderAdapter = new OrderAdapter(orderlist);
                    recyclerView1.setAdapter(orderAdapter);


                })
                .addOnFailureListener(e -> Log.e("ElecLog", "Error fetching orders", e));


        return rootView;
    }
}

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final ArrayList<Orders> orderArrayList;

    public OrderAdapter(ArrayList<Orders> orderArrayList) {
        this.orderArrayList = orderArrayList;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderId, orderDate, orderAmount, orderStatus;
        public RecyclerView recyclerViewList;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.textView40);
            orderDate = itemView.findViewById(R.id.textView42);
            orderAmount = itemView.findViewById(R.id.textView41);
            orderStatus = itemView.findViewById(R.id.textView43);
            recyclerViewList = itemView.findViewById(R.id.recyclerView6);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order_list, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        Orders orders = orderArrayList.get(position);
        holder.orderId.setText(orders.getOrderId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(orders.getDate());

        holder.orderDate.setText(String.valueOf(formattedDate));
        String amount = "Rs. " + orders.getAmount();

        holder.orderAmount.setText(amount);
        if (orders.getStatus() == 1) {
            holder.orderStatus.setText("Ordered");
        } else if (orders.getStatus() == 2) {
            holder.orderStatus.setText("Sent to Packing");
        } else if (orders.getStatus() == 3) {
            holder.orderStatus.setText("Ready for Delivery");
        }


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(holder.itemView.getContext());
        layoutManager2.setOrientation(RecyclerView.VERTICAL);
        holder.recyclerViewList.setLayoutManager(layoutManager2);
        ArrayList<OrdersProducts> orderProductslist = orders.getProducts();
        OrderProductAdapter orderProductAdapter = new OrderProductAdapter(orderProductslist);
        holder.recyclerViewList.setAdapter(orderProductAdapter);


    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }
}


class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder> {

    private final ArrayList<OrdersProducts> orderProductArrayList;

    public OrderProductAdapter(ArrayList<OrdersProducts> orderProductArrayList) {
        this.orderProductArrayList = orderProductArrayList;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productQty;
        public ImageView productImage;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.textView29);
            productQty = itemView.findViewById(R.id.textView30);
            productPrice = itemView.findViewById(R.id.textView31);
            productImage = itemView.findViewById(R.id.imageView8);

        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchased_items_fragment_list, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        OrdersProducts ordersProducts = orderProductArrayList.get(position);

        holder.productName.setText(ordersProducts.getProduct_name());
        String price = "Rs. " + ordersProducts.getPrice();
        String qty = ordersProducts.getQuantity() + " Items";
        holder.productPrice.setText(price);
        holder.productQty.setText(qty);


        String imageUrl = ordersProducts.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);


    }

    @Override
    public int getItemCount() {
        return orderProductArrayList.size();
    }
}
