package lk.webstudio.elecshop.navigations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.webstudio.elecshop.AllOrdersActivity;
import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;
import lk.webstudio.elecshop.model.Product;
import lk.webstudio.elecshop.model.UserOrders;


public class OrdersFragment extends Fragment {

    RecyclerView recyclerView2;
    ArrayList<UserOrders> orderlist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);

        Button viewAllBtn = rootView.findViewById(R.id.button44);
        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(rootView.getContext(), AllOrdersActivity.class);
                startActivity(i);
            }
        });

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        recyclerView2 = rootView.findViewById(R.id.recyclerView12);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        try {
            firestore
                    .collection("orders")

                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                QuerySnapshot ordersSnap = task.getResult();
                                for (QueryDocumentSnapshot qs : ordersSnap) {
                                    String orderDate = sdf.format(qs.getDate("date_ordered"));
                                    if (orderDate.equals(formattedDate)) {
                                        List<Map<String, Object>> products = (List<Map<String, Object>>) qs.get("products");

                                        for (Map<String, Object> product : products) {

                                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                            firebaseFirestore
                                                    .collection("products")
                                                    .whereEqualTo("product_code", String.valueOf(product.get("productCode")))
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {

                                                                QuerySnapshot querySnap = task.getResult();
                                                                for (QueryDocumentSnapshot qs1 : querySnap) {
                                                                    if (qs1.getString("user_id").equals(MainActivity.userLogId)) {
                                                                        Log.i("ElecLog", String.valueOf(product.get("productName")));

                                                                        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();
                                                                        firestore1
                                                                                .collection("user")
                                                                                .whereEqualTo(FieldPath.documentId(), qs1.getString("user_id"))
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            QuerySnapshot querySnap1 = task.getResult();
                                                                                            for (QueryDocumentSnapshot qs2 : querySnap1) {

                                                                                                try {
                                                                                                    orderlist.add(new UserOrders(

                                                                                                            qs1.getId(),
                                                                                                            qs1.getString("product_name"),
                                                                                                            qs1.getString("product_code"),
                                                                                                            qs1.getString("image_url"),
                                                                                                            Integer.parseInt(String.valueOf(product.get("productQty"))),
                                                                                                            Double.parseDouble(String.valueOf(product.get("productPrice"))),
                                                                                                            qs2.getId(),
                                                                                                            qs2.getString("firstName"),
                                                                                                            qs2.getString("lastName"),
                                                                                                            qs2.getString("email"),
                                                                                                            qs2.getString("mobile"),
                                                                                                            String.valueOf(qs.getLong("order_id")),
                                                                                                            qs.getDate("date_ordered"),
                                                                                                            Integer.parseInt(String.valueOf(qs.getLong("status")))


                                                                                                    ));
                                                                                                } catch (
                                                                                                        Exception e) {
                                                                                                    Log.i("ElecLog", String.valueOf(e));
                                                                                                }

                                                                                            }

                                                                                        }

                                                                                        allFetched();
                                                                                    }

                                                                                });


                                                                    }

                                                                }

                                                            }

                                                        }

                                                    });

                                        }
                                    }

                                }


                            }


                        }

                    });


        } catch (Exception e) {
            Log.i("ElecLog", String.valueOf(e));
        }


        return rootView;
    }


    public void allFetched() {

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.VERTICAL);
        recyclerView2.setLayoutManager(layoutManager2);

        UserOrdersAdapter userOrders = new UserOrdersAdapter(orderlist);
        recyclerView2.setAdapter(userOrders);
        userOrders.notifyDataSetChanged();
    }


}

class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.UserOrdersViewHolder> {

    private final ArrayList<UserOrders> userOrdersArrayList;

    public UserOrdersAdapter(ArrayList<UserOrders> userOrdersArrayList) {

        this.userOrdersArrayList = userOrdersArrayList;
    }

    static class UserOrdersViewHolder extends RecyclerView.ViewHolder {
        public TextView productCode, productName, productQty;
        public ImageView productImage;
        public Button viewBtn;

        public UserOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            productCode = itemView.findViewById(R.id.textView65);
            productName = itemView.findViewById(R.id.textView66);
            productQty = itemView.findViewById(R.id.textView67);
            productImage = itemView.findViewById(R.id.imageView15);
            viewBtn = itemView.findViewById(R.id.button49);

        }
    }

    @NonNull
    @Override
    public UserOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_fragment_list, parent, false);
        return new UserOrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrdersViewHolder holder, int position) {
        UserOrders userOrder = userOrdersArrayList.get(position);


        try {
            holder.productCode.setText(String.valueOf(userOrder.getProductCode()));

            holder.productName.setText(userOrder.getProductName());

            holder.productQty.setText(userOrder.getQty() + " Items");


            String imageUrl = userOrder.getImageUrl();
            if (imageUrl != null && imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            Glide.with(holder.productImage.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.product2)
                    .error(R.drawable.product2)
                    .into(holder.productImage);

            holder.viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    View dialogView = inflater.inflate(R.layout.alert_order_view, null);

                    TextView alertProductName = dialogView.findViewById(R.id.textView73);
                    TextView alertProductCode = dialogView.findViewById(R.id.textView75);
                    TextView alertProductQty = dialogView.findViewById(R.id.textView79);
                    TextView alertProductUnitPrice = dialogView.findViewById(R.id.textView80);
                    TextView alertProductTotalPrice = dialogView.findViewById(R.id.textView81);
                    TextView alertProductCustomer = dialogView.findViewById(R.id.textView83);
                    Button callBtn = dialogView.findViewById(R.id.button4);
                    Button readyOrder = dialogView.findViewById(R.id.button);

                    if(userOrder.getOrderStatus() == 2){
                        readyOrder.setBackgroundColor(v.getResources().getColor(R.color.gray));
                        readyOrder.setTextColor(v.getResources().getColor(R.color.darkGray));
                        readyOrder.setText(R.string.onDelivery);
                    }

                    alertProductName.setText(userOrder.getProductName());
                    alertProductCode.setText(userOrder.getProductCode());
                    alertProductQty.setText(String.valueOf(userOrder.getQty()));
                    alertProductUnitPrice.setText(String.valueOf(userOrder.getPrice()));
                    Double total = userOrder.getQty() * userOrder.getPrice();
                    alertProductTotalPrice.setText(String.valueOf(total));
                    alertProductCustomer.setText(userOrder.getCustomerFname()+" "+userOrder.getCustomerLname());
                    callBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + userOrder.getCustomerMobile())); // Add "tel:" prefix
                            dialogView.getContext().startActivity(intent);
                        }
                    });
                    readyOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            HashMap<String ,Object> updates = new HashMap<>();
                            updates.put("status",2);
                            firestore
                                    .collection("orders")
                                    .whereEqualTo("order_id", Integer.parseInt(userOrder.getOrderId()))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnap = task.getResult();
                                                for (QueryDocumentSnapshot qs1 : querySnap) {
                                                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                                    firebaseFirestore
                                                            .collection("orders")
                                                            .document(qs1.getId())
                                                            .update(updates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    readyOrder.setBackgroundColor(v.getResources().getColor(R.color.gray));
                                                                    readyOrder.setTextColor(v.getResources().getColor(R.color.darkGray));
                                                                    readyOrder.setText(R.string.onDelivery);
                                                                    userOrder.setOrderStatus(2);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });

                        }
                    });



                    builder.setView(dialogView)

                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    // Set the negative button text color (MUST be called after dialog.show())
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(v.getContext().getResources().getColor(R.color.darkOrange));


                }
            });
        } catch (Exception e) {
            Log.i("ElecLog", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return userOrdersArrayList.size();
    }



}
