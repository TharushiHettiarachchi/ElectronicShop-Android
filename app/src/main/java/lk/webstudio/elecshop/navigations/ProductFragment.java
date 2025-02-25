package lk.webstudio.elecshop.navigations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

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
                                        if(qs.getLong("status") != 4){
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
                                                            qs.getString("image_url"),
                                                            qs.getString("product_category")
                                                    )
                                            );
                                        }

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
        public ImageButton editBtn, deleteBtn;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productCode = itemView.findViewById(R.id.product_code);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQty = itemView.findViewById(R.id.productQty);
            productImage = itemView.findViewById(R.id.productImage);
            editBtn = itemView.findViewById(R.id.imageButton2);
            deleteBtn = itemView.findViewById(R.id.imageButton3);
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
        int nowPosition = position;
        Product product = productArrayList.get(nowPosition);
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

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.alert_update_product, null);

                EditText alertProductName = dialogView.findViewById(R.id.editTextText8);
                EditText alertProductQty = dialogView.findViewById(R.id.editTextNumber);
                EditText alertProductPrice = dialogView.findViewById(R.id.editTextNumber2);
                Button updateBtn = dialogView.findViewById(R.id.button2);


                alertProductName.setText(product.getProduct_name());
                alertProductQty.setText(String.valueOf(product.getQuantity()));
                alertProductPrice.setText(String.valueOf(product.getPrice()));


                builder.setView(dialogView)

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> updatelist = new HashMap<>();
                        updatelist.put("product_name", alertProductName.getText().toString());
                        updatelist.put("price", Double.parseDouble(alertProductPrice.getText().toString()));
                        updatelist.put("quantity", Integer.parseInt(alertProductQty.getText().toString()));

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore
                                .collection("products")
                                .document(product.getProduct_id())
                                .update(updatelist)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(dialogView.getContext(), "Product Updated", Toast.LENGTH_LONG).show();
                                        product.setPrice(Integer.parseInt(alertProductPrice.getText().toString()));
                                        product.setQuantity(Integer.parseInt(alertProductQty.getText().toString()));
                                        product.setProduct_name(alertProductName.getText().toString());
                                        notifyItemChanged(position);
                                        dialog.cancel();


                                    }
                                });


                    }
                });


                dialog.show();
                // Set the negative button text color (MUST be called after dialog.show())
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(v.getContext().getResources().getColor(R.color.darkOrange));


            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());




                builder
                        .setMessage(R.string.message1)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, Object> deleteUpdate = new HashMap<>();
                                deleteUpdate.put("status", 4);
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore
                                        .collection("products")
                                        .document(product.getProduct_id())
                                        .update(deleteUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(builder.getContext(), "Product Deleted", Toast.LENGTH_LONG).show();
                                                int position1 = productArrayList.indexOf(product);
                                                if (position1 != -1) {
                                                    productArrayList.remove(position1);
                                                    notifyItemRemoved(position1);
                                                    notifyItemRangeChanged(position1, productArrayList.size()); // Refresh UI
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }
                        );

                AlertDialog dialog = builder.create();
                dialog.show();
                // Set the negative button text color (MUST be called after dialog.show())
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(v.getContext().getResources().getColor(R.color.darkOrange));


            }
        });


    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}
