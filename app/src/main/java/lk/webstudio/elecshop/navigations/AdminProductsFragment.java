package lk.webstudio.elecshop.navigations;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import androidx.core.app.ActivityCompat;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


import lk.webstudio.elecshop.R;

import lk.webstudio.elecshop.model.Product;


public class AdminProductsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_products, container, false);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("products")
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
                                                    qs.getString("image_url"),
                                                    qs.getString("product_category")
                                            )
                                    );
                                }

                                RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerview15);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                layoutManager.setOrientation(RecyclerView.VERTICAL);
                                recyclerView1.setLayoutManager(layoutManager);

                                ProductAdapter3 productAdapter = new ProductAdapter3(getContext(),productList);
                                recyclerView1.setAdapter(productAdapter);

                            } catch (Exception e) {
                                Log.e("ElecLog", "Error parsing products", e);
                            }
                        }
                    }
                });


        return rootView;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("ElecLog", "onRequestPermissionsResult triggered");

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getContext(), "Permission denied! Unable to make calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

class ProductAdapter3 extends RecyclerView.Adapter<ProductAdapter3.ProductViewHolder3> {
    String userMobile = "";
    private final ArrayList<Product> productArrayList;
    private final Context context;


    public ProductAdapter3(Context context, ArrayList<Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    static class ProductViewHolder3 extends RecyclerView.ViewHolder {
        public TextView productCode, productName, productPrice, productQty;
        public ImageView productImage;
        public Button viewBtn2;
        public SwitchCompat productStatus;

        public ProductViewHolder3(@NonNull View itemView) {
            super(itemView);
            productCode = itemView.findViewById(R.id.textView71);
            productName = itemView.findViewById(R.id.textView84);
            viewBtn2 = itemView.findViewById(R.id.button3);
            productImage = itemView.findViewById(R.id.imageView16);
            productStatus = itemView.findViewById(R.id.switch3);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_product_fragment_list, parent, false);
        return new ProductViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder3 holder, int position) {
        Product product = productArrayList.get(position);
        holder.productCode.setText(product.getProduct_code());
        holder.productName.setText(product.getProduct_name());

        if (product.getStatus() == 1) {
            holder.productStatus.setChecked(true);
        } else {
            holder.productStatus.setChecked(false);
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

        holder.productStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            HashMap<String, Object> productUpdates = new HashMap<>();
            productUpdates.put("status", isChecked ? 1 : 2);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("products")
                    .document(product.getProduct_id())
                    .update(productUpdates)
                    .addOnCompleteListener(task -> Log.i("ElecLog", isChecked ? "Enabled" : "Disabled"));
        });

        holder.viewBtn2.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.alert_admin_product_view, null);

            TextView alertProductID = dialogView.findViewById(R.id.textView86);
            TextView alertProductName = dialogView.findViewById(R.id.textView90);
            TextView alertProductCode = dialogView.findViewById(R.id.textView88);
            TextView alertProductCategory = dialogView.findViewById(R.id.textView92);
            TextView alertProductPrice = dialogView.findViewById(R.id.textView94);
            TextView alertProductQty = dialogView.findViewById(R.id.textView96);
            TextView alertProductStatus = dialogView.findViewById(R.id.textView98);
            TextView alertProductUser = dialogView.findViewById(R.id.textView100);
            Button callBtn = dialogView.findViewById(R.id.button5);
            ImageView productImage = dialogView.findViewById(R.id.imageView17);

            alertProductName.setText(product.getProduct_name());
            alertProductCode.setText(product.getProduct_code());
            alertProductQty.setText(String.valueOf(product.getQuantity()));
            alertProductPrice.setText(String.valueOf(product.getPrice()));
            alertProductStatus.setText(String.valueOf(product.getStatus()));
            alertProductCategory.setText(String.valueOf(product.getCategory()));
            alertProductID.setText(String.valueOf(product.getProduct_id()));

            String imageUrl1 = product.getImage_url();
            if (imageUrl1 != null && imageUrl1.startsWith("http://")) {
                imageUrl1 = imageUrl1.replace("http://", "https://");
            }

            Glide.with(productImage.getContext())
                    .load(imageUrl1)
                    .placeholder(R.drawable.product2)
                    .error(R.drawable.product2)
                    .into(productImage);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("user")
                    .whereEqualTo(FieldPath.documentId(), product.getUser_id())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot ordersSnap = task.getResult();
                            for (QueryDocumentSnapshot qs : ordersSnap) {
                                alertProductUser.setText(qs.getString("firstName") + " " + qs.getString("lastName"));
                                userMobile = qs.getString("mobile");
                            }
                        }
                    });

            callBtn.setOnClickListener(v1 -> {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + userMobile));
                        context.startActivity(intent);
                    } else {
                        if (context instanceof Activity) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 100);
                        } else {
                            Log.i("ElecLog", "Context is not an Activity.");
                        }
                    }
                } catch (Exception e) {
                    Log.i("ElecLog", String.valueOf(e));
                }
            });

            builder.setView(dialogView)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.darkOrange));
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }
}
