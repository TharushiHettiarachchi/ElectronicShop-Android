package lk.webstudio.elecshop;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import lk.webstudio.elecshop.model.CartList;
import lk.webstudio.elecshop.model.OrderList;

import lk.webstudio.elecshop.model.User;



public class ConfirmActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11010;
    private TextView textView;
    private final ArrayList<CartList> cartList = new ArrayList<>();
    private final ArrayList<OrderList> orderDetailList = new ArrayList<>();
    User userList;
    double subTotal;
    int uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("cart")
                .whereEqualTo("user_id", MainActivity.userLogId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        List<String> productIds = new ArrayList<>();
                        List<QueryDocumentSnapshot> cartDocs = new ArrayList<>();

                        for (QueryDocumentSnapshot qs : task.getResult()) {
                            String productId = qs.getString("product_id");
                            if (productId != null) {
                                productIds.add(productId);
                                cartDocs.add(qs);
                            }
                        }

                        if (!productIds.isEmpty()) {
                            firestore.collection("products")
                                    .whereIn(FieldPath.documentId(), productIds)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            double total = 0.0;
                                            for (QueryDocumentSnapshot cartItem : cartDocs) {
                                                for (DocumentSnapshot product : task2.getResult()) {
                                                    if (cartItem.getString("product_id").equals(product.getId())) {
                                                        int price = product.getLong("price") != null ? product.getLong("price").intValue() : 0;
                                                        int quantity = cartItem.getLong("quantity") != null ? cartItem.getLong("quantity").intValue() : 0;
                                                        total = total + (price * quantity);
                                                        cartList.add(new CartList(
                                                                product.getId(),
                                                                product.getString("product_code"),
                                                                product.getString("product_name"),
                                                                price,
                                                                product.getLong("quantity") != null ? product.getLong("quantity").intValue() : 0,
                                                                quantity,
                                                                cartItem.getString("user_id"),
                                                                product.getString("image_url"),
                                                                cartItem.getId()
                                                        ));
                                                        orderDetailList.add(new OrderList(
                                                                product.getString("product_name"),
                                                                quantity,
                                                                product.getString("product_code"),
                                                                price,
                                                                product.getString("image_url")
                                                        ));


                                                        break;
                                                    }
                                                }
                                            }

                                            subTotal = total;
                                            RecyclerView recyclerView1 = findViewById(R.id.recyclerViewConfirm);
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(ConfirmActivity.this);
                                            layoutManager.setOrientation(RecyclerView.VERTICAL);
                                            recyclerView1.setLayoutManager(layoutManager);

                                            ConfirmAdapter confirmAdapter = new ConfirmAdapter(cartList);
                                            recyclerView1.setAdapter(confirmAdapter);

                                            Button orderBtn = findViewById(R.id.order);
                                            orderBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Random random = new Random();
                                                    uniqueID = 100000 + random.nextInt(900000);


                                                    firestore.collection("user")
                                                            .whereEqualTo(FieldPath.documentId(), MainActivity.userLogId)
                                                            .get()
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    try {
                                                                        QuerySnapshot querySnapshot = task.getResult();
                                                                        for (QueryDocumentSnapshot qs : querySnapshot) {

                                                                            Map<String, Object> location = (Map<String, Object>) qs.get("location");
                                                                            double latitude = 0.0;
                                                                            double longitude = 0.0;
                                                                            if (location != null) {
                                                                                latitude = location.get("latitude") != null ? (double) location.get("latitude") : 0.0;
                                                                                longitude = location.get("longitude") != null ? (double) location.get("longitude") : 0.0;
                                                                            }

                                                                            userList = new User(
                                                                                    qs.getString("firstName"),
                                                                                    qs.getString("lastName"),
                                                                                    qs.getString("email"),
                                                                                    qs.getString("mobile"),
                                                                                    qs.getString("password"),
                                                                                    qs.getDate("registered_on"),
                                                                                    latitude,
                                                                                    longitude,
                                                                                   Integer.parseInt( String.valueOf(qs.getLong("status"))),
                                                                                    qs.getId()
                                                                            );
                                                                        }

                                                                        InitRequest req = new InitRequest();
                                                                        req.setMerchantId("1221108 ");

                                                                        // Merchant ID
                                                                        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                                                                        req.setAmount(subTotal);
                                                                        req.setOrderId(String.valueOf(uniqueID));        // Unique Reference ID
                                                                        req.setItemsDescription("Order " + uniqueID);  // Item description title
                                                                        req.setCustom1("This is the custom message 1");
                                                                        req.setCustom2("This is the custom message 2");
                                                                        req.getCustomer().setFirstName(userList.getFirstName());
                                                                        req.getCustomer().setLastName(userList.getLastName());
                                                                        req.getCustomer().setEmail(userList.getEmail());
                                                                        req.getCustomer().setPhone(userList.getMobile());
                                                                        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                                                                        req.getCustomer().getAddress().setCity("Colombo");
                                                                        req.getCustomer().getAddress().setCountry("Sri Lanka");

                                                                        Intent intent = new Intent(ConfirmActivity.this, PHMainActivity.class);
                                                                        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                                                                        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                                                                        startActivityForResult(intent, PAYHERE_REQUEST);

                                                                    } catch (Exception e) {
                                                                        Log.e("ElecLog", "Error fetching user data: " + e.getMessage());
                                                                    }
                                                                } else {
                                                                    Log.e("ElecLog", "Error fetching user data: " + task.getException());
                                                                }
                                                            });


                                                }
                                            });


                                        } else {
                                            Log.e("ElecLog", "Error fetching products", task2.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("CartFragment", "Error fetching cart", task.getException());
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            if (resultCode == Activity.RESULT_OK) {
                if (response != null && response.isSuccess()) {
                    String msg = "Activity result: " + response.getData().toString();
                    Log.d("ElecLog", msg);
                    Log.i("ElecLog", msg);
                    Date date = new Date();
                    HashMap<String, Object> orderData = new HashMap<>();
                    orderData.put("customer_id", MainActivity.userLogId);
                    orderData.put("amount", subTotal);
                    orderData.put("date_ordered", date);
                    orderData.put("order_id", uniqueID);
                    orderData.put("status", 1);
                    orderData.put("products", orderDetailList);


                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore
                            .collection("orders")
                            .add(orderData)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Log.i("ElecLog", "Successfully order data added");
                                    FirebaseFirestore firestore2 = FirebaseFirestore.getInstance();
                                    for (CartList item : cartList) {
                                        double availableQty = item.getQuantity_available();
                                        double purchasedQty = item.getQuantity_ordered();
                                        double remainingQty = item.getQuantity_available() - item.getQuantity_ordered();

                                        firestore2
                                                .collection("products")
                                                .document(item.getProduct_id())
                                                .update("quantity", remainingQty)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.i("ElecLog", "Qty successfully changed");


                                                        firestore2
                                                                .collection("cart")
                                                                .whereEqualTo("product_id", item.getProduct_id())
                                                                .whereEqualTo("user_id", item.getCustomer_id())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            try {
                                                                                QuerySnapshot querySnapshot = task.getResult();
                                                                                for (QueryDocumentSnapshot qs : querySnapshot) {
                                                                                    String cartId = qs.getId();
                                                                                    firestore2
                                                                                            .collection("cart")
                                                                                            .document(cartId)
                                                                                            .delete()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    Log.i("ElecLog", "Sucessfully removed");
                                                                                                }
                                                                                            });
                                                                                }
                                                                            } catch (
                                                                                    Exception e) {
                                                                                Log.e("ElecLog", String.valueOf(e));
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                    }
                                                });

                                    }


                                }
                            });


                } else {
                    String msg = "Result: " + (response != null ? response.toString() : "no response");
                    Log.d("ElecLog", msg);


                }

                Intent intent = new Intent(ConfirmActivity.this, HomeActivity.class);
                startActivity(intent);


            } else if (resultCode == Activity.RESULT_CANCELED) {
                textView.setText(response != null ? response.toString() : "User canceled the request");
                Log.i("ElecLog", response.toString());
            }
        }
    }
}

class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.ConfirmViewHolder> {

    private final ArrayList<CartList> confirmArrayList;

    public ConfirmAdapter(ArrayList<CartList> confirmArrayList) {
        this.confirmArrayList = confirmArrayList;
    }

    static class ConfirmViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQty;

        public ConfirmViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textView34);
            productPrice = itemView.findViewById(R.id.textView36);
            productQty = itemView.findViewById(R.id.textView35);
        }
    }

    @NonNull
    @Override
    public ConfirmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_confirm_list, parent, false);
        return new ConfirmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmViewHolder holder, int position) {
        CartList cartList = confirmArrayList.get(position);
        double  price = cartList.getPrice() * cartList.getQuantity_ordered();
        holder.productName.setText(cartList.getProduct_name());
        holder.productPrice.setText("Rs. " + price);
        holder.productQty.setText(String.valueOf(cartList.getQuantity_ordered()));
    }

    @Override
    public int getItemCount() {
        return confirmArrayList.size();
    }
}