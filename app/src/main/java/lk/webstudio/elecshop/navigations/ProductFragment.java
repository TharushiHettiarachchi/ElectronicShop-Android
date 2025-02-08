package lk.webstudio.elecshop.navigations;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.Product;

public class ProductFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);

        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore
                    .collection("products")
                    .whereEqualTo("user_id", MainActivity.userLogId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    ArrayList<Product> productList = new ArrayList<>();

                                    for (QueryDocumentSnapshot qs : querySnapshot) {
                                        productList.add(
                                                new Product(
                                                        qs.getId(),
                                                        qs.getString("product_code"),
                                                        qs.getString("product_name"),
                                                        qs.getLong("price") != null ? qs.getLong("price").intValue() : 0,
                                                        qs.getLong("quantity") != null ? qs.getLong("quantity").intValue() : 0,
                                                        qs.getLong("status") != null ? qs.getLong("status").intValue() : 0,
                                                        qs.getString("user_id"),
                                                        qs.getString("date_added"),
                                                        qs.getString("image_url")
                                                )
                                        );
                                    }

                                    RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerView1);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                                    recyclerView1.setLayoutManager(layoutManager);

                                    ProductAdapter productAdapter = new ProductAdapter(productList);
                                    recyclerView1.setAdapter(productAdapter);

                                } catch (Exception e) {
                                    Log.e("ElecLog", "Error parsing products", e);
                                }
                            }
                        }
                    });

            FloatingActionButton fab = rootView.findViewById(R.id.fab1);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView1, new AddProductFragment());
                    fragmentTransaction.setReorderingAllowed(true);
                    fragmentTransaction.commit();
                }
            });
        } catch (Exception e) {
            Log.e("ElecLog", "Error initializing Firebase", e);
        }

        return rootView;
    }
}

class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final ArrayList<Product> productArrayList;

    public ProductAdapter(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productCode, productName, productPrice, productQty;
        public ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productCode = itemView.findViewById(R.id.product_code);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQty = itemView.findViewById(R.id.productQty);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_fragment_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.productCode.setText(product.getProduct_code());
        holder.productName.setText(product.getProduct_name());
        holder.productPrice.setText("Rs. " + product.getPrice());
        holder.productQty.setText(product.getQuantity() + " Items");


        String imageUrl = product.getImage_url();
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
        return productArrayList.size();
    }
}
