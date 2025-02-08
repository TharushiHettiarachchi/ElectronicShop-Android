package lk.webstudio.elecshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
public static String userLogId = "OzoQGaLRynVR4Snm5neE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("lk.webstudio.elecshop.userlist", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        // If email and password are found, redirect to drawer_nav activity
        if (savedEmail != null && savedPassword != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity so user cannot go back to login screen
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView link1 = findViewById(R.id.link1);
        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        EditText email = findViewById(R.id.emailText);
        EditText password = findViewById(R.id.passwordText);
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter the Email", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter your Password", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore
                            .collection("user")
                            .whereEqualTo("email", email.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null) {
                                            for (QueryDocumentSnapshot qs : querySnapshot) {
                                                String storedPassword = qs.getString("password");
                                                if (storedPassword.equals(password.getText().toString())) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("lk.webstudio.elecshop.userlist", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("email", email.getText().toString());
                                                    editor.putString("password", password.getText().toString());
                                                    editor.apply();
                                                    userLogId = qs.getId();
                                                    Log.i("Electronic Shop", "User Login Successful");
                                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                                    email.setText("");
                                                    password.setText("");
                                                    Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Log.i("Electronic Shop", "Invalid Password");
                                                    Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        } else {
                                            Log.i("Electronic Shop", "Email Not Found");
                                            Toast.makeText(MainActivity.this, "Email Not Found", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Log.i("Electronic Shop", "Something went wrong");
                                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });


    }
}