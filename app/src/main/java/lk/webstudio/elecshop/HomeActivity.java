package lk.webstudio.elecshop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.webstudio.elecshop.model.Product;
import lk.webstudio.elecshop.navigations.CartFragment;
import lk.webstudio.elecshop.navigations.HomeFragment;
import lk.webstudio.elecshop.navigations.LogoutFragment;
import lk.webstudio.elecshop.navigations.ProductFragment;
import lk.webstudio.elecshop.navigations.ProfileFragment;
import lk.webstudio.elecshop.navigations.PurchasedItemFragment;
import lk.webstudio.elecshop.navigations.WishlistFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout1);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        FrameLayout frameLayout = findViewById(R.id.frame_layout1);
        NavigationView navigationView = findViewById(R.id.navigation_view1);


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("user")
                .whereEqualTo(FieldPath.documentId(), MainActivity.userLogId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();


                            try {

                                for (QueryDocumentSnapshot qs : querySnapshot) {
                                    TextView fullName = findViewById(R.id.textView2);
                                    TextView emailHeader = findViewById(R.id.textView6);
                                    fullName.setText(qs.getString("firstName") + " " + qs.getString("lastName"));
                                    emailHeader.setText(qs.getString("email"));

                                    if (qs.getLong("status") == 1) {
                                        Log.i("ElecLog", "status 1");

                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            NotificationChannel notificationChannel = new NotificationChannel("C1", "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
                                            notificationManager.createNotificationChannel(notificationChannel);
                                        }


                                        Intent intent = new Intent(HomeActivity.this,HomeActivity.class );
                                        intent.putExtra("openProfileFragment", true);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                                HomeActivity.this,
                                                100,
                                                intent,
                                                PendingIntent.FLAG_IMMUTABLE
                                        );

                                        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                                                R.drawable.iconlogo,
                                                "View",
                                                pendingIntent
                                        ).build();

                                        Notification notification = new NotificationCompat.Builder(HomeActivity.this, "C1")
                                                .setContentTitle("Welcome to Electronic Shop!")
                                                .setContentText("Please Update your Profile and Setup your account.")
                                                .setSmallIcon(R.drawable.iconlogo)
                                                .addAction(action)
                                                .build();
                                        notificationManager.notify(1, notification);
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("status", 2);
                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                        try {
                                            firebaseFirestore
                                                    .collection("user")
                                                    .document(qs.getId())
                                                    .update(updates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.i("ElecLog", "Status updated");
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            Log.i("ElecLog", String.valueOf(e));
                                        }


                                    }





                                }
                            } catch (RuntimeException e) {
                                Log.i("ElecLog", String.valueOf(e));
                            }
                        }
                    }
                });


        FragmentManager fragmentManager = getSupportFragmentManager();


        TextView tootlText = findViewById(R.id.toolbarTxt);
        tootlText.setText("Home");



        if (savedInstanceState == null) { // Avoid reloading on rotation
            if (getIntent().getBooleanExtra("openProfileFragment", false)) {
                // Open ProfileFragment when coming from the notification
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView1,  ProfileFragment.class,null)
                        .setReorderingAllowed(true)
                        .commit();
                tootlText.setText("Profile");
            } else {
                // Default: Open HomeFragment
               fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView1,  HomeFragment.class,null)
                       .setReorderingAllowed(true)
                        .commit();
            }
        }






        ImageButton imgMenuBtn = findViewById(R.id.imageButtonmenu);
        imgMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.close();
              }else{
                  drawerLayout.open();
              }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("ElecLog", item.toString());

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (item.toString().equals("Home")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  HomeFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                } else if (item.toString().equals("Profile")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  ProfileFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                } else if (item.toString().equals("Products")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  ProductFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                } else if (item.toString().equals("Cart")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  CartFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                }else if (item.toString().equals("Wishlist")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  WishlistFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                }else if (item.toString().equals("Purchased Items")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  PurchasedItemFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                }else if (item.toString().equals("Logout")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView1,  LogoutFragment.class,null)
                            .setReorderingAllowed(true)
                            .commit();
                }

                tootlText.setText(item.toString());
//                fragmentTransaction.setReorderingAllowed(true);
//                fragmentTransaction.commit();
                drawerLayout.close();

                return true;
            }
        });

    }





}