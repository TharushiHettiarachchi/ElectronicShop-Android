package lk.webstudio.elecshop.navigations;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.Product;


public class HomeFragment extends Fragment {
    Spinner spinner2;
    EditText searchTxt;
    ArrayList<Product> productList2;
    ArrayList<Product> searchList;
    ArrayList<Product> comboList;
    RecyclerView recyclerView2;
    String TypedTxt = "";
    String selectedCombo = "Select";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView2 = rootView.findViewById(R.id.recyclerView2);
        spinner2 = rootView.findViewById(R.id.spinner2);
        searchTxt = rootView.findViewById(R.id.editTextText2);


        loadSpinner();
        fetchProductData(rootView);


        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TypedTxt = "";
                TypedTxt = s.toString();


                fetchProductData2();
                searchProducts(TypedTxt, selectedCombo);
            }

            @Override
            public void afterTextChanged(Editable s) {
                fetchProductData2();

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedText = parent.getItemAtPosition(position).toString();

                selectedCombo = selectedText;
                searchProducts(TypedTxt, selectedCombo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Log.e("ElecLog", "Selected:nothing ");
            }
        });


        ImageButton imgBtn = rootView.findViewById(R.id.imageButton5);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypedTxt = "";
                selectedCombo = "Select";
                if (spinner2.getAdapter() != null && spinner2.getAdapter().getCount() > 1) {
                    spinner2.setSelection(0);
                }
                searchTxt.setText("");
                searchProducts(TypedTxt, selectedCombo);


            }
        });

        return rootView;
    }

    public void fetchProductData2() {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("products").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> productTask) {
                            if (productTask.isSuccessful()) {
                                try {
                                    QuerySnapshot productSnapshot = productTask.getResult();
                                    productList2 = new ArrayList<>();
                                    List<String> productIds = new ArrayList<>();

                                    for (QueryDocumentSnapshot qs : productSnapshot) {
                                        String productId = qs.getId();
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
                                                qs.getString("image_url"),
                                                qs.getString("product_category")
                                        ));
                                    }


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
    }

    public void fetchProductData(View rootView) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("products").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> productTask) {
                            if (productTask.isSuccessful()) {
                                try {
                                    QuerySnapshot productSnapshot = productTask.getResult();
                                    productList2 = new ArrayList<>();
                                    List<String> productIds = new ArrayList<>();

                                    for (QueryDocumentSnapshot qs : productSnapshot) {
                                        String productId = qs.getId();
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
                                                qs.getString("image_url"),
                                                qs.getString("product_category")

                                        ));
                                    }


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
    }

    public void loadSpinner() {

        FirebaseFirestore firestore2 = FirebaseFirestore.getInstance();
        firestore2
                .collection("product_category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                QuerySnapshot querySnapshot = task.getResult();
                                String[] categories = new String[querySnapshot.size() + 1];
                                int i = 0;
                                categories[i] = "Select";
                                i = i + 1;
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
                            } catch (Exception e) {
                                Log.i("ElecLog", String.valueOf(e));
                            }
                        }
                    }
                });
    }

    public void searchProducts(String searchTxtValue, String searchComboValue) {
        searchList = new ArrayList<>();
        comboList = new ArrayList<>();
        searchList.clear();
        comboList.clear();
        for (Product product : productList2) {
            try {

                if (product.getProduct_name().toLowerCase().contains(searchTxtValue) && searchComboValue.equals("Select")) {


                    searchList.add(product);

                } else if (!searchComboValue.equals("Select")) {


                    if(product.getProduct_name().toLowerCase().contains(searchTxtValue) && product.getCategory().contains(searchComboValue)){

                        searchList.add(product);
                    }

                }
            } catch (Exception e) {
                Log.i("ElecLog", String.valueOf(e));
            }
        }


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.VERTICAL);
        recyclerView2.setLayoutManager(layoutManager2);

        ProductAdapter2 productAdapter2 = new ProductAdapter2(searchList);
        recyclerView2.setAdapter(productAdapter2);


//        Log.i("ElecLog", "----------------------------------------");
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


        updateProductStatus(holder, product);

        // Set image for product
        String imageUrl = product.getImage_url();
        if (imageUrl != null && imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replace("http://", "https://");
        }

        Glide.with(holder.productImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.product2)
                .error(R.drawable.product2)
                .into(holder.productImage);

        // Cart button functionality
        holder.cartBtn.setOnClickListener(v -> addToCart(v, product, holder));

        // Wishlist button functionality
        holder.wishlistBtn.setOnClickListener(v -> handleWishlist(v, product, holder));
    }

    private void updateProductStatus(ProductViewHolder holder, Product product) {
        if (product.getStatus() == 0) {
            holder.cartBtn.setEnabled(true);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkOrange));
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart));
        } else if (product.getStatus() == 1) {
            holder.cartBtn.setEnabled(false);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart));
        } else if (product.getStatus() == 2) {
            holder.cartBtn.setEnabled(true);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.darkOrange));
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart1));
        } else if (product.getStatus() == 3) {
            holder.cartBtn.setEnabled(false);
            holder.cartBtn.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.heart1));
        }
    }

    private void addToCart(View v, Product product, ProductViewHolder holder) {
        HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("product_id", product.getProduct_id());
        cartMap.put("quantity", 1);
        cartMap.put("user_id", MainActivity.userLogId);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("cart")
                .add(cartMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holder.cartBtn.setEnabled(false);
                        holder.cartBtn.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.gray));
                        Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("ElecLog", "Error adding to cart", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.e("ElecLog", "Error adding to cart", e));
    }

    private void handleWishlist(View v, Product product, ProductViewHolder holder) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("wishlist")
                .document(product.getProduct_id());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Product is already in wishlist, remove it
                    docRef.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.heart));
                            Toast.makeText(v.getContext(), "Removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ElecLog", "Error removing from wishlist", deleteTask.getException());
                        }
                    });
                } else {
                    // Product is not in wishlist, add it
                    docRef.set(new HashMap<String, Object>() {{
                        put("product_id", product.getProduct_id());
                        put("user_id", MainActivity.userLogId);
                    }}).addOnCompleteListener(addTask -> {
                        if (addTask.isSuccessful()) {
                            holder.wishlistBtn.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.heart1));
                            Toast.makeText(v.getContext(), "Added to wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("ElecLog", "Error adding to wishlist", addTask.getException());
                        }
                    });
                }
            } else {
                Log.e("ElecLog", "Error checking wishlist", task.getException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }


}

