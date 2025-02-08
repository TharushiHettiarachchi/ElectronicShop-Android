package lk.webstudio.elecshop.navigations;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.CartList;

public class CartFragment extends Fragment {
    private final ArrayList<CartList> cartList = new ArrayList<>();
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView3;
    private FirebaseFirestore firestore;
    private double subTotal = 0.00;
    private TextView totalPrice;
    private static final int PAYHERE_REQUEST = 1101;  // Define request code

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

        // Initialize ActivityResultLauncher for payment response
        paymentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) result.getData()
                                .getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (response != null) {
                            if (response.isSuccess()) {
                                Log.d(TAG, "Payment Success: " + response.getData().toString());
                                Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "Payment Failed: " + response.toString());
                                Toast.makeText(requireContext(), "Payment Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Payment Canceled!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Button placeOrderBtn = rootView.findViewById(R.id.placeBtn);
        placeOrderBtn.setOnClickListener(v -> processPayment());

        return rootView;
    }

    private void processPayment() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1221108");  // Merchant ID
        req.setCurrency("LKR");        // Currency code
        req.setAmount(1000.00);        // Final Amount
        req.setOrderId("230000123");   // Unique Reference ID
        req.setItemsDescription("Door bell wireless");
        req.setCustom1("Custom message 1");
        req.setCustom2("Custom message 2");
        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        Intent intent = new Intent(requireContext(), PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        paymentResultLauncher.launch(intent);
    }

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
                                                                product.getString("image_url")
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

        String imageUrl = cartItem.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);

        holder.cartDeleteBtn.setOnClickListener(v -> {
            // Handle item removal from cart
            cartArrayList.remove(position);
            notifyItemRemoved(position);
            calculateSubtotal();  // Recalculate subtotal after deletion
            totalPrice.setText("Rs. " + String.format("%.2f", subTotal));
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
    }
}
