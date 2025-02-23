package lk.webstudio.elecshop.navigations;

import static androidx.core.app.NotificationCompat.getColor;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.webstudio.elecshop.R;


public class DashboardFragment extends Fragment {
    int userCount;
    int productsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = task.getResult().size();
                            userCount = count - 1;
                            TextView users = rootView.findViewById(R.id.textView5);

                            users.setText(String.valueOf(userCount));
                        }
                    }
                });
        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();
        firestore1
                .collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count1 = task.getResult().size();
                            productsCount = count1 - 1;
                            TextView products = rootView.findViewById(R.id.textView44);



                            products.setText(String.valueOf(productsCount));

                        }
                    }
                });
        FirebaseFirestore firestore2 = FirebaseFirestore.getInstance();
        firestore2
                .collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot ordersSnap = task.getResult();
                            Double total = 0.0;
                            int qtyTotal = 0;
                            double january = 0.0;
                            double february = 0.0;
                            double march = 0.0;
                            double april = 0.0;
                            double may = 0.0;
                            double june = 0.0;
                            double july = 0.0;
                            double august = 0.0;
                            double september = 0.0;
                            double october = 0.0;
                            double november = 0.0;
                            double december = 0.0;

                            for (QueryDocumentSnapshot qs : ordersSnap) {
                                String orderDate = sdf.format(qs.getDate("date_ordered"));
                                if (orderDate.equals(formattedDate)) {
                                    total = total + qs.getDouble("amount");


                                    List<Map<String, Object>> products = (List<Map<String, Object>>) qs.get("products");
                                    for (Map<String, Object> product : products) {
                                        qtyTotal = qtyTotal + Integer.parseInt(String.valueOf(product.get("productQty")));
                                    }


                                }
                                try {

                                    // Ensure this format matches SimpleDateFormat
                                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date = sdf1.parse(orderDate);
                                    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                                    String month = monthFormat.format(date);


                                    if(month.equals("January")){
                                        january = january + qs.getDouble("amount");
                                    }else if(month.equals("February")){
                                        february = february + qs.getDouble("amount");
                                    }else if(month.equals("March")){
                                        march = march + qs.getDouble("amount");
                                    }else if(month.equals("April")){
                                        april = april + qs.getDouble("amount");
                                    }else if(month.equals("May")){
                                        may = may + qs.getDouble("amount");
                                    }else if(month.equals("June")){
                                        june = june + qs.getDouble("amount");
                                    }else if(month.equals("July")){
                                        july = july + qs.getDouble("amount");
                                    }else if(month.equals("August")){
                                        august = august + qs.getDouble("amount");
                                    }else if(month.equals("September")){
                                        september = september + qs.getDouble("amount");
                                    }else if(month.equals("October")){
                                        october = october + qs.getDouble("amount");
                                    }else if(month.equals("November")){
                                        november = november + qs.getDouble("amount");
                                    }else if(month.equals("December")){
                                        december = december + qs.getDouble("amount");
                                    }



                                } catch (Exception e) {
                                    Log.i("ElecLog", String.valueOf(e));
                                }


                            }
                            BarChart barChart1 = rootView.findViewById(R.id.barchart1);
                            ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
                            barEntryArrayList.add(new BarEntry(1, Float.parseFloat(String.valueOf(january))));
                            barEntryArrayList.add(new BarEntry(2, Float.parseFloat(String.valueOf(february))));
                            barEntryArrayList.add(new BarEntry(3, Float.parseFloat(String.valueOf(march))));
                            barEntryArrayList.add(new BarEntry(4, Float.parseFloat(String.valueOf(april))));
                            barEntryArrayList.add(new BarEntry(5, Float.parseFloat(String.valueOf(may))));
                            barEntryArrayList.add(new BarEntry(6, Float.parseFloat(String.valueOf(june))));
                            barEntryArrayList.add(new BarEntry(7, Float.parseFloat(String.valueOf(july))));
                            barEntryArrayList.add(new BarEntry(8, Float.parseFloat(String.valueOf(august))));
                            barEntryArrayList.add(new BarEntry(9, Float.parseFloat(String.valueOf(september))));
                            barEntryArrayList.add(new BarEntry(10, Float.parseFloat(String.valueOf(october))));
                            barEntryArrayList.add(new BarEntry(11, Float.parseFloat(String.valueOf(november))));
                            barEntryArrayList.add(new BarEntry(12, Float.parseFloat(String.valueOf(december))));


                            BarDataSet barDataSet = new BarDataSet(barEntryArrayList, null);


                            barDataSet.setGradientColor(
                                    ContextCompat.getColor(requireContext(), R.color.darkOrange),
                                    ContextCompat.getColor(requireContext(), R.color.lightOrange)
                            );


                            barDataSet.setBarShadowColor(R.color.transparent);
                            BarData barData = new BarData();
                            barData.addDataSet(barDataSet);
                            barChart1.setPinchZoom(false);
                            barChart1.setScaleXEnabled(false);
                            barChart1.setScaleYEnabled(false);
                            barChart1.animateY(2000, Easing.EaseInCubic);
                            barChart1.setDescription(null);
                            barChart1.setDrawGridBackground(false);
                            barData.setBarWidth(Float.parseFloat("0.5"));
                            barChart1.setFitBars(true);

                            barChart1.setGridBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent));


                            barChart1.setData(barData);
                            barChart1.invalidate();
                            TextView totalView = rootView.findViewById(R.id.textView47);
                            TextView itemView = rootView.findViewById(R.id.textView48);
                            totalView.setText("Rs." + " " + total);
                            itemView.setText(qtyTotal + " Items");
                        }
                    }
                });


        return rootView;
    }
}