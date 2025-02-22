package lk.webstudio.elecshop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import lk.webstudio.elecshop.model.Product;

public class SingleProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String productID = intent.getStringExtra("productID");


        TextView productCode = findViewById(R.id.textView55);
        TextView productName = findViewById(R.id.textView56);
        TextView productPrice = findViewById(R.id.textView58);
        TextView productQty = findViewById(R.id.textView57);
        TextView productOwner = findViewById(R.id.textView60);
        TextView productCategory = findViewById(R.id.textView59);
        TextView productDate = findViewById(R.id.textView61);
        ImageView productImg = findViewById(R.id.imageView14);
        Button cartBtn = findViewById(R.id.buttonCart);
        Button wishlistBtn = findViewById(R.id.buttonWishlist);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        try {
            firestore
                    .collection("products")
                    .whereEqualTo(FieldPath.documentId(), productID.toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot productSnapshot = task.getResult();
                                for (QueryDocumentSnapshot qs : productSnapshot) {


                                    productName.setText(qs.getString("product_name"));
                                    productPrice.setText("Rs. " + qs.getDouble("price"));
                                    productCode.setText(qs.getString("product_code"));
                                    productCategory.setText(qs.getString("product_category"));

                                    productDate.setText(qs.getString("date_added"));
                                    productQty.setText(qs.getLong("quantity") + " Items");

                                    String imageUrl = qs.getString("image_url");
                                    if (imageUrl != null && imageUrl.startsWith("http://")) {
                                        imageUrl = imageUrl.replace("http://", "https://");
                                    }

                                    Glide.with(productImg.getContext())
                                            .load(imageUrl)
                                            .placeholder(R.drawable.product2)
                                            .error(R.drawable.product2)
                                            .into(productImg);

                                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                    firebaseFirestore
                                            .collection("user")
                                            .whereEqualTo(FieldPath.documentId(), qs.getString("user_id"))
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot productSnapshot = task.getResult();
                                                        for (QueryDocumentSnapshot qs1 : productSnapshot) {
                                                            productOwner.setText("Seller: " + qs1.getString("firstName") + " " + qs1.getString("lastName"));
                                                        }
                                                    }
                                                }
                                            });


                                }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.i("ElecLog", String.valueOf(e));
        }

        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();
        firestore1
                .collection("cart")
                .whereEqualTo("product_id", productID)
                .whereEqualTo("user_id", MainActivity.userLogId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot cartSnapshot = task.getResult();
                            if (cartSnapshot.isEmpty()) {
                                Log.i("ElecLog", "Not Added");
                                cartBtn.setEnabled(true);
                                cartBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        HashMap<String, Object> cartAdd = new HashMap<>();
                                        cartAdd.put("product_id", productID);
                                        cartAdd.put("user_id", MainActivity.userLogId);
                                        cartAdd.put("quantity", 1);

                                        firebaseFirestore
                                                .collection("cart")
                                                .add(cartAdd)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        cartBtn.setEnabled(false);
                                                        cartBtn.setBackgroundColor(getColor(R.color.gray));
                                                        cartBtn.setTextColor(getColor(R.color.darkGray));
                                                        cartBtn.setText("Added to Cart");
                                                    }
                                                });
                                    }
                                });

                            } else {
                                Log.i("ElecLog", "Added");
                                cartBtn.setEnabled(false);
                                cartBtn.setBackgroundColor(getColor(R.color.gray));
                                cartBtn.setTextColor(getColor(R.color.darkGray));
                                cartBtn.setText("Added to Cart");

                            }
                        }
                    }
                });


        FirebaseFirestore firestore3 = FirebaseFirestore.getInstance();
        firestore3
                .collection("wishlist")
                .whereEqualTo("product_id", productID)
                .whereEqualTo("user_id", MainActivity.userLogId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot cartSnapshot = task.getResult();
                            if (cartSnapshot.isEmpty()) {
                                Log.i("ElecLog", "Not Added");
                                wishlistBtn.setEnabled(true);
                                wishlistBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        HashMap<String, Object> cartAdd = new HashMap<>();
                                        cartAdd.put("product_id", productID);
                                        cartAdd.put("user_id", MainActivity.userLogId);


                                        firebaseFirestore
                                                .collection("wishlist")
                                                .add(cartAdd)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        wishlistBtn.setEnabled(false);
                                                        wishlistBtn.setBackgroundColor(getColor(R.color.gray));
                                                        wishlistBtn.setTextColor(getColor(R.color.darkGray));
                                                        wishlistBtn.setText("Added to Wishlist");
                                                    }
                                                });
                                    }
                                });

                            } else {
                                Log.i("ElecLog", "Added");
                                wishlistBtn.setEnabled(false);
                                wishlistBtn.setBackgroundColor(getColor(R.color.gray));
                                wishlistBtn.setTextColor(getColor(R.color.darkGray));
                                wishlistBtn.setText("Added to Wishlist");

                            }
                        }
                    }
                });


    }
}