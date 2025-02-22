package lk.webstudio.elecshop.navigations;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lk.webstudio.elecshop.ConfirmActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.CartList;
import lk.webstudio.elecshop.model.User;

public class CartFragment extends Fragment {
    private final ArrayList<CartList> cartList = new ArrayList<>();
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView3;
    private FirebaseFirestore firestore;
    private double subTotal = 0.00;
    private TextView totalPrice;
    User userList;
    private static final int PAYHERE_REQUEST = 1101;

    private ActivityResultLauncher<Intent> paymentResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView3 = rootView.findViewById(R.id.recyclerView3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));

        totalPrice = rootView.findViewById(R.id.totalPrice);
        cartAdapter = new CartAdapter(cartList, totalPrice);
        recyclerView3.setAdapter(cartAdapter);

        firestore = FirebaseFirestore.getInstance();
        fetchCartData();

//        // Initialize ActivityResultLauncher for payment response
//        paymentResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) result.getData()
//                                .getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
//                        if (response != null) {
//                            if (response.isSuccess()) {
//                                Log.d(TAG, "Payment Success: " + response.getData().toString());
//                                Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Log.d(TAG, "Payment Failed: " + response.toString());
//                                Toast.makeText(requireContext(), "Payment Failed!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Payment Canceled!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );

        Button placeOrderBtn = rootView.findViewById(R.id.placeBtn);
        placeOrderBtn.setOnClickListener(v -> {

            Intent i = new Intent(getContext(), ConfirmActivity.class);
            startActivity(i);
//            fetchUserDataAndProceedToPayment();
        });

        return rootView;
    }

//    private void fetchUserDataAndProceedToPayment() {
//        firestore.collection("user")
//                .whereEqualTo(FieldPath.documentId(), MainActivity.userLogId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        try {
//                            QuerySnapshot querySnapshot = task.getResult();
//                            for (QueryDocumentSnapshot qs : querySnapshot) {
//                                // Extract location object first
//                                Map<String, Object> location = (Map<String, Object>) qs.get("location");
//                                double latitude = 0.0;
//                                double longitude = 0.0;
//                                if (location != null) {
//                                    latitude = location.get("latitude") != null ? (double) location.get("latitude") : 0.0;
//                                    longitude = location.get("longitude") != null ? (double) location.get("longitude") : 0.0;
//                                }
//
//                                userList = new User(
//                                        qs.getString("firstName"),
//                                        qs.getString("lastName"),
//                                        qs.getString("email"),
//                                        qs.getString("mobile"),
//                                        qs.getString("password"),
//                                        qs.getDate("registered_on"),
//                                        latitude,
//                                        longitude
//                                );
//                            }
//
//                            proceedToPayment();
//
//                        } catch (Exception e) {
//                            Log.e("ElecLog", "Error fetching user data: " + e.getMessage());
//                        }
//                    } else {
//                        Log.e("ElecLog", "Error fetching user data: " + task.getException());
//                    }
//                });
//    }

//    private void proceedToPayment() {
//
//
//        Random random = new Random();
//        int uniqueID = 100000 + random.nextInt(900000);
//
//        cartAdapter.calculateSubtotal();
//        // Proceed with payment
//        InitRequest req = new InitRequest();
//        req.setMerchantId("1221108");
//        req.setCurrency("LKR");
//        req.setAmount(subTotal);
//        req.setOrderId(String.valueOf(uniqueID));
//        req.setItemsDescription("Order" + uniqueID);
//        req.getCustomer().setFirstName(userList.getFirstName());
//        req.getCustomer().setLastName(userList.getLastName());
//        req.getCustomer().setEmail(userList.getEmail());
//        req.getCustomer().setPhone(userList.getMobile());
//        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
//        req.getCustomer().getAddress().setCity("Colombo");
//        req.getCustomer().getAddress().setCountry("Sri Lanka");
//
//        Intent intent = new Intent(requireContext(), PHMainActivity.class);
//        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
//        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
//        paymentResultLauncher.launch(intent);
//    }


    public void fetchCartData() {
        firestore.collection("cart")
                .whereEqualTo("user_id", MainActivity.userLogId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartList.clear();
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
                                            subTotal = 0.00;
                                            for (QueryDocumentSnapshot cartItem : cartDocs) {
                                                for (DocumentSnapshot product : task2.getResult()) {
                                                    if (cartItem.getString("product_id").equals(product.getId())) {
                                                        int price = product.getLong("price") != null ? product.getLong("price").intValue() : 0;
                                                        int quantity = cartItem.getLong("quantity") != null ? cartItem.getLong("quantity").intValue() : 0;

                                                        cartList.add(new CartList(
                                                                product.getId(),
                                                                cartItem.getString("product_code"),
                                                                product.getString("product_name"),
                                                                price,
                                                                product.getLong("quantity") != null ? product.getLong("quantity").intValue() : 0,
                                                                quantity,
                                                                cartItem.getString("user_id"),
                                                                product.getString("image_url"),
                                                                cartItem.getId()
                                                        ));

                                                        subTotal += price * quantity;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(() -> {
                                                    cartAdapter.notifyDataSetChanged();
                                                    totalPrice.setText("Rs. " + String.format("%.2f", subTotal));
                                                });
                                            }
                                        } else {
                                            Log.e("CartFragment", "Error fetching products", task2.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("CartFragment", "Error fetching cart", task.getException());
                    }
                });
    }
}


class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final ArrayList<CartList> cartArrayList;
    private final TextView totalPrice;
    private double subTotal;

    public CartAdapter(ArrayList<CartList> cartArrayList, TextView totalPrice) {
        this.cartArrayList = cartArrayList;
        this.totalPrice = totalPrice;

        // Calculate initial subtotal
        calculateSubtotal();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice;
        public EditText productQty;
        public ImageView productImage;
        public ImageButton cartDeleteBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textView16);
            productPrice = itemView.findViewById(R.id.textView17);
            productQty = itemView.findViewById(R.id.QtyEdit);
            productImage = itemView.findViewById(R.id.imageView5);
            cartDeleteBtn = itemView.findViewById(R.id.cartDeleteBtn);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_fragment, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartList cartItem = cartArrayList.get(position);
        holder.productName.setText(cartItem.getProduct_name());
        holder.productPrice.setText("Rs. " + cartItem.getPrice());
        holder.productQty.setText(String.valueOf(cartItem.getQuantity_ordered()));
        holder.productQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (holder.productQty.getText().toString().isEmpty()) {
                        holder.productQty.setText(String.valueOf(cartItem.getQuantity_ordered()));

                    }else if(cartItem.getQuantity_available() < Integer.parseInt(holder.productQty.getText().toString())){
                        holder.productQty.setText(String.valueOf(cartItem.getQuantity_ordered()));
                    }

                }

            }
        });
        holder.productQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        int enteedQty = Integer.parseInt(s.toString());
                        if (cartItem.getQuantity_available() >= enteedQty) {
                            HashMap<String,Object> cartUpdates = new HashMap<>();
                            cartUpdates.put("quantity",Integer.parseInt(holder.productQty.getText().toString()));
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            try{
                                firestore
                                        .collection("cart")
                                        .document(cartItem.getCartID())
                                        .update(cartUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.i("ElecLog","Qunatity Updated");
                                            }
                                        });
                                cartItem.setQuantity_ordered(Integer.parseInt(holder.productQty.getText().toString()));
                                calculateSubtotal();
                            } catch (Exception e) {
                                Log.i("ElecLog",String.valueOf(e));
                            }
                            Log.i("ElecLog", "Suffient");


                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Only " + cartItem.getQuantity_available() + " Items Available", Toast.LENGTH_LONG).show();
                            Log.i("ElecLog", "Only " + cartItem.getQuantity_available() + " Items Available");
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(holder.itemView.getContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        String imageUrl = cartItem.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);

        holder.cartDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("cart")
                        .whereEqualTo("user_id", MainActivity.userLogId)  // Match user ID
                        .whereEqualTo("product_id", cartItem.getProduct_id())  // Match product ID
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    firestore.collection("cart")
                                            .document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("ElecLog", "Cart item removed successfully");
                                                cartArrayList.remove(position);
                                                notifyItemRemoved(position);
                                                calculateSubtotal();  // Recalculate subtotal after deletion
                                                totalPrice.setText("Rs. " + String.format("%.2f", subTotal));
                                                Toast.makeText(v.getContext(), "Removed from cart", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("ElecLog", "Error removing item from cart", e);
                                                Toast.makeText(v.getContext(), "Failed to remove", Toast.LENGTH_LONG).show();
                                            });
                                }
                            } else {
                                Log.e("ElecLog", "Error finding cart item", task.getException());
                            }
                        });


            }
        });
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public void calculateSubtotal() {
        subTotal = 0.00;
        for (CartList cartItem : cartArrayList) {
            subTotal += cartItem.getPrice() * cartItem.getQuantity_ordered();
        }
        totalPrice.setText(String.valueOf(subTotal));
    }
}
