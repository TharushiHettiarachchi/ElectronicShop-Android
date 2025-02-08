package lk.webstudio.elecshop;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
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

import lk.webstudio.elecshop.model.Product;
import lk.webstudio.elecshop.navigations.CartFragment;
import lk.webstudio.elecshop.navigations.HomeFragment;
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
                                }
                            } catch (RuntimeException e) {
                                Log.i("ElecLog", String.valueOf(e));
                            }
                        }
                    }
                });

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout1);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        FrameLayout frameLayout = findViewById(R.id.frame_layout1);
        NavigationView navigationView = findViewById(R.id.navigation_view1);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragmentContainerView1, HomeFragment.class, null);
        TextView tootlText = findViewById(R.id.toolbarTxt);
        tootlText.setText("Home");

        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("ElecLog", item.toString());

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (item.toString().equals("Home")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, HomeFragment.class, null);
                } else if (item.toString().equals("Profile")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, ProfileFragment.class, null);
                } else if (item.toString().equals("Products")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, ProductFragment.class, null);
                } else if (item.toString().equals("Cart")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, CartFragment.class, null);
                }else if (item.toString().equals("Wishlist")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, WishlistFragment.class, null);
                }else if (item.toString().equals("Purchased Items")) {
                    fragmentTransaction.replace(R.id.fragmentContainerView1, PurchasedItemFragment.class, null);
                }

                tootlText.setText(item.toString());
                fragmentTransaction.setReorderingAllowed(true);
                fragmentTransaction.commit();
                drawerLayout.close();

                return true;
            }
        });

    }
}