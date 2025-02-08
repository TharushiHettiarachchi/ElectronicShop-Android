package lk.webstudio.elecshop.navigations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;

import lk.webstudio.elecshop.model.WishlistList;

public class WishlistFragment extends Fragment {

    private final ArrayList<WishlistList> wishlistList = new ArrayList<>();
    private WishlistAdapter wishlistAdapter;

    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerView4 = rootView.findViewById(R.id.recyclerView4);
        recyclerView4.setLayoutManager(new LinearLayoutManager(getContext()));

        wishlistAdapter = new WishlistAdapter(wishlistList);
        recyclerView4.setAdapter(wishlistAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchWishlistData();

        return rootView;
    }

    private void fetchWishlistData() {
        firestore.collection("wishlist")
                .whereEqualTo("user_id", MainActivity.userLogId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> productIds = new ArrayList<>();
                        List<QueryDocumentSnapshot> wishlistDocs = new ArrayList<>();

                        for (QueryDocumentSnapshot qs : task.getResult()) {
                            String productId = qs.getString("product_id");
                            if (productId != null) {
                                productIds.add(productId);
                                wishlistDocs.add(qs);
                            }
                        }

                        if (!productIds.isEmpty()) {
                            firestore.collection("products")
                                    .whereIn(FieldPath.documentId(), productIds)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            List<QueryDocumentSnapshot> productDocs = new ArrayList<>();

                                            for (DocumentSnapshot doc : task2.getResult().getDocuments()) {
                                                if (doc.exists()) {
                                                    productDocs.add((QueryDocumentSnapshot) doc);
                                                }
                                            }

                                            for (QueryDocumentSnapshot wishlistItem : wishlistDocs) {
                                                String productId = wishlistItem.getString("product_id");

                                                firestore.collection("cart")
                                                        .whereEqualTo("user_id", MainActivity.userLogId)
                                                        .whereEqualTo("product_id", productId)
                                                        .get()
                                                        .addOnCompleteListener(cartTask -> {
                                                            int status = 0; // Default status

                                                            if (cartTask.isSuccessful() && !cartTask.getResult().isEmpty()) {
                                                                status = 1; // Product found in cart
                                                            }

                                                            for (QueryDocumentSnapshot product : productDocs) {
                                                                if (productId.equals(product.getId())) {
                                                                    wishlistList.add(new WishlistList(
                                                                            product.getId(),
                                                                            product.getString("product_code"),
                                                                            product.getString("product_name"),
                                                                            product.getLong("price") != null ? product.getLong("price").intValue() : 0,
                                                                            product.getLong("quantity") != null ? product.getLong("quantity").intValue() : 0,
                                                                            wishlistItem.getLong("quantity") != null ? wishlistItem.getLong("quantity").intValue() : 0,
                                                                            wishlistItem.getString("user_id"),
                                                                            product.getString("image_url"),
                                                                            status
                                                                    ));
                                                                    break;
                                                                }
                                                            }

                                                            // Update UI
                                                            if (getActivity() != null) {
                                                                getActivity().runOnUiThread(() -> wishlistAdapter.notifyDataSetChanged());
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.e("WishlistFragment", "Error fetching products", task2.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("WishlistFragment", "Error fetching Wishlist", task.getException());
                    }
                });
    }

}


class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {
    private final ArrayList<WishlistList> wishlistArrayList;

    public WishlistAdapter(ArrayList<WishlistList> wishlistArrayList) {
        this.wishlistArrayList = wishlistArrayList;
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productCode;

        public ImageView productImage;
        public ImageButton wishlistDeleteBtn, wishlistCartBtn;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textView22);
            productCode = itemView.findViewById(R.id.textView21);
            productPrice = itemView.findViewById(R.id.textView23);
            wishlistDeleteBtn = itemView.findViewById(R.id.wishlistDeleteBtn);
            wishlistCartBtn = itemView.findViewById(R.id.wishlistCartBtn);
            productImage = itemView.findViewById(R.id.imageView6);
        }
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_list_fragment, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistList wishlistItem = wishlistArrayList.get(position);
        holder.productName.setText(wishlistItem.getProduct_name());
        holder.productCode.setText(wishlistItem.getProduct_code());
        holder.productPrice.setText("Rs. " + wishlistItem.getPrice());
        if (wishlistItem.getStatus() == 1) {
            holder.wishlistCartBtn.setEnabled(false);
            holder.wishlistCartBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cart2));
            holder.wishlistCartBtn.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.disbled_btn_bg));
        }

        String imageUrl = wishlistItem.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);
        holder.wishlistDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                firestore.collection("wishlist")
                        .whereEqualTo("user_id", MainActivity.userLogId)  // Match user ID
                        .whereEqualTo("product_id", wishlistItem.getProduct_id())  // Match product ID
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    firestore.collection("wishlist")
                                            .document(document.getId())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("ElecLog", "Wishlist item removed successfully");
                                                int position = holder.getBindingAdapterPosition();
                                                if (position != RecyclerView.NO_POSITION) {
                                                    wishlistArrayList.remove(position);
                                                    notifyItemRemoved(position);
                                                }
                                                Toast.makeText(v.getContext(), "Removed from Wishlist", Toast.LENGTH_LONG).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("ElecLog", "Error removing item from wishlist", e);
                                                Toast.makeText(v.getContext(), "Failed to remove", Toast.LENGTH_LONG).show();
                                            });
                                }
                            } else {
                                Log.e("ElecLog", "Error finding cart item", task.getException());
                            }
                        });
            }
        });

        holder.wishlistCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("product_id", wishlistItem.getProduct_id());
                cartMap.put("quantity", 1);
                cartMap.put("user_id", MainActivity.userLogId);


                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore
                        .collection("cart")
                        .add(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    holder.wishlistCartBtn.setEnabled(false);
                                    holder.wishlistCartBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cart2));
                                    holder.wishlistCartBtn.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.disbled_btn_bg));
                                    Toast.makeText(v.getContext(), "Added to Cart Successfully", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishlistArrayList.size();
    }
}
