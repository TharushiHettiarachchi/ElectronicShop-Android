package lk.webstudio.elecshop.navigations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lk.webstudio.elecshop.HomeActivity;
import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.Product;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Spinner spinner2 = rootView.findViewById(R.id.spinner2);
        FirebaseFirestore firestore2 = FirebaseFirestore.getInstance();
        firestore2
                .collection("product_category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            String[] categories = new String[querySnapshot.size()];
                            int i = 0;
                            for (QueryDocumentSnapshot qs : querySnapshot) {
                                categories[i] = qs.getString("category_name");
                                i++;
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    categories
                            );
                            spinner2.setAdapter(arrayAdapter);
                        }
                    }
                });

        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Fetch all products
            firestore.collection("products").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> productTask) {
                            if (productTask.isSuccessful()) {
                                try {
                                    QuerySnapshot productSnapshot = productTask.getResult();
                                    ArrayList<Product> productList2 = new ArrayList<>();
                                    List<String> productIds = new ArrayList<>();

                                    for (QueryDocumentSnapshot qs : productSnapshot) {
                                        String productId = qs.getId(); // Get product document ID
                                        productIds.add(productId);

                                        productList2.add(new Product(
                                                qs.getId(),
                                                qs.getString("product_code"),
                                                qs.getString("product_name"),
                                                qs.getLong("price") != null ? qs.getLong("price").intValue() : 0,
                                                qs.getLong("quantity") != null ? qs.getLong("quantity").intValue() : 0,
                                                0,  // Default status (no cart, no wishlist)
                                                qs.getString("user_id"),
                                                qs.getString("date_added"),
                                                qs.getString("image_url")
                                        ));
                                    }

                                    // Fetch cart items and wishlist items for the logged-in user
                                    firestore.collection("cart")
                                            .whereEqualTo("user_id", MainActivity.userLogId)
                                            .get()
                                            .addOnCompleteListener(cartTask -> {
                                                if (cartTask.isSuccessful()) {
                                                    List<String> cartProductIds = new ArrayList<>();
                                                    for (QueryDocumentSnapshot cartDoc : cartTask.getResult()) {
                                                        String cartProductId = cartDoc.getString("product_id");
                                                        if (cartProductId != null) {
                                                            cartProductIds.add(cartProductId);
                                                        }
                                                    }

                                                    firestore.collection("wishlist")
                                                            .whereEqualTo("user_id", MainActivity.userLogId)
                                                            .get()
                                                            .addOnCompleteListener(wishlistTask -> {
                                                                if (wishlistTask.isSuccessful()) {
                                                                    List<String> wishlistProductIds = new ArrayList<>();
                                                                    for (QueryDocumentSnapshot wishlistDoc : wishlistTask.getResult()) {
                                                                        String wishlistProductId = wishlistDoc.getString("product_id");
                                                                        if (wishlistProductId != null) {
                                                                            wishlistProductIds.add(wishlistProductId);
                                                                        }
                                                                    }

                                                                    // Update product status based on cart and wishlist data
                                                                    for (Product product : productList2) {
                                                                        boolean inCart = cartProductIds.contains(product.getProduct_id());
                                                                        boolean inWishlist = wishlistProductIds.contains(product.getProduct_id());

                                                                        if (inCart && inWishlist) {
                                                                            product.setStatus(3); // Added to both cart and wishlist
                                                                        } else if (inCart) {
                                                                            product.setStatus(1); // Added to cart, no wishlist
                                                                        } else if (inWishlist) {
                                                                            product.setStatus(2); // No cart, added to wishlist
                                                                        } else {
                                                                            product.setStatus(0); // No cart, no wishlist
                                                                        }
                                                                    }

                                                                    // Display updated products in RecyclerView
                                                                    RecyclerView recyclerView2 = rootView.findViewById(R.id.recyclerView2);
                                                                    LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
                                                                    layoutManager2.setOrientation(RecyclerView.VERTICAL);
                                                                    recyclerView2.setLayoutManager(layoutManager2);

                                                                    ProductAdapter2 productAdapter2 = new ProductAdapter2(productList2);
                                                                    recyclerView2.setAdapter(productAdapter2);

                                                                } else {
                                                                    Log.e("ElecLog", "Error fetching wishlist data", wishlistTask.getException());
                                                                }
                                                            });

                                                } else {
                                                    Log.e("ElecLog", "Error fetching cart data", cartTask.getException());
                                                }
                                            });

                                } catch (Exception e) {
                                    Log.e("ElecLog", "Error parsing products", e);
                                }
                            } else {
                                Log.e("ElecLog", "Error fetching products", productTask.getException());
                            }
                        }
                    });

        } catch (Exception e) {
            Log.e("ElecLog", "Error initializing Firebase", e);
        }


        return rootView;
    }
}

class ProductAdapter2 extends RecyclerView.Adapter<ProductAdapter2.ProductViewHolder> {

    private final ArrayList<Product> productArrayList;

    public ProductAdapter2(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productCode, productName, productPrice, productQty;
        public ImageView productImage;
        public Button cartBtn;
        public ImageButton wishlistBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productCode = itemView.findViewById(R.id.textView4);
            productName = itemView.findViewById(R.id.textView13);
            productPrice = itemView.findViewById(R.id.textView14);
            productQty = itemView.findViewById(R.id.textView15);
            productImage = itemView.findViewById(R.id.imageView4);
            cartBtn = itemView.findViewById(R.id.cartBtn);
            wishlistBtn = itemView.findViewById(R.id.wishlistBtn);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_fragment_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.productCode.setText(product.getProduct_code());
        holder.productName.setText(product.getProduct_name());
        holder.productPrice.setText("Rs. " + product.getPrice());
        holder.productQty.setText(product.getQuantity() + " Items");


        if (product.getStatus() == 0) {
            holder.cartBtn.setEnabled(true);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkOrange));
           // holder.wishlistBtn.setEnabled(true);
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart));
        } else if (product.getStatus() == 1) {
            holder.cartBtn.setEnabled(false);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
           // holder.wishlistBtn.setEnabled(true);
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart));
        } else if (product.getStatus() == 2) {
            holder.cartBtn.setEnabled(true);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkOrange));
           // holder.wishlistBtn.setEnabled(false);
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart1));
        }else if (product.getStatus() == 3) {
            holder.cartBtn.setEnabled(false);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
           // holder.wishlistBtn.setEnabled(false);
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart1));
        }



        String imageUrl = product.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);

        holder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("product_id", product.getProduct_id());
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
                                    holder.cartBtn.setEnabled(false);
                                    holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
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

        holder.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(product.getStatus() == 0 || product.getStatus() == 1){
                   HashMap<String, Object> wishlistMap = new HashMap<>();
                   wishlistMap.put("product_id", product.getProduct_id());

                   wishlistMap.put("user_id", MainActivity.userLogId);

                   FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                   firestore
                           .collection("wishlist")
                           .add(wishlistMap)
                           .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                               @Override
                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                   if (task.isSuccessful()) {
                                       holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart1));

                                       Toast.makeText(v.getContext(), "Added to Wishlist Successfully", Toast.LENGTH_LONG).show();
                                   }
                               }
                           })
                           .addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                               }
                           });
               }else{
                   FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                   firestore.collection("wishlist")
                           .whereEqualTo("user_id", MainActivity.userLogId)  // Match user ID
                           .whereEqualTo("product_id", product.getProduct_id())  // Match product ID
                           .get()
                           .addOnCompleteListener(task -> {
                               if (task.isSuccessful()) {
                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                       firestore.collection("wishlist")
                                               .document(document.getId()) // Delete the document by its ID
                                               .delete()
                                               .addOnSuccessListener(aVoid -> {
                                                   Log.d("ElecLog", "Wishlist item removed successfully");
                                                   holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart));
                                                   Toast.makeText(v.getContext(), "Removed from Wishlist", Toast.LENGTH_LONG).show();
                                               })
                                               .addOnFailureListener(e -> {
                                                   Log.e("ElecLog", "Error removing item from wishlist", e);
                                                   Toast.makeText(v.getContext(), "Failed to remove", Toast.LENGTH_LONG).show();
                                               });
                                   }
                               } else {
                                   Log.e("ElecLog", "Error finding wishlist item", task.getException());
                               }
                           });
               }

            }
        });

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}
