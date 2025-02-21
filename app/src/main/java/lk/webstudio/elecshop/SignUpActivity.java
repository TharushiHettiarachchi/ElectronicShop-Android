package lk.webstudio.elecshop;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView link2 = findViewById(R.id.link2);
        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        EditText firstName = findViewById(R.id.firstName);
        EditText lastName = findViewById(R.id.lastName);
        EditText mobile = findViewById(R.id.mobile);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter your First Name", Toast.LENGTH_LONG).show();
                } else if (lastName.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter your Last Name", Toast.LENGTH_LONG).show();
                } else if (mobile.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter your Mobile Number", Toast.LENGTH_LONG).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter your Email", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter your Password", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
Date date = new Date();
                    HashMap<String, Object> userHashMap = new HashMap<>();
                    userHashMap.put("firstName", firstName.getText().toString());
                    userHashMap.put("lastName", lastName.getText().toString());
                    userHashMap.put("mobile", mobile.getText().toString());
                    userHashMap.put("email", email.getText().toString());
                    userHashMap.put("password", password.getText().toString());
                    userHashMap.put("registered_on", date);
                    userHashMap.put("status", 1);



                    firestore
                            .collection("user")
                            .whereEqualTo("email", email.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            Log.i("Electronic Shop", "User Already exist");
                                            Toast.makeText(SignUpActivity.this, "User Already Exist", Toast.LENGTH_LONG).show();
                                        } else {
                                            firestore
                                                    .collection("user")
                                                    .add(userHashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            EmailSender.sendEmail(email.getText().toString(), "Welcome to Electronic Shop", "Dear "+firstName.getText().toString()+" "+lastName.getText().toString()+" ,\n" +
                                                                            "\n" +
                                                                            "\n" +
                                                                            "Thank you for registering with ElecShop! We are thrilled to have you on board. Explore a wide range of electronic components, tools, and accessories tailored to meet all your project needs. Enjoy a seamless shopping experience, exclusive discounts, and fast delivery right to your doorstep. If you have any questions or need assistance, feel free to reach out to our support team at electronishoppvt@gmail.com.\n" +
                                                                            "Happy shopping!\n" +
                                                                            "The ElecShop Team",
                                                                    new EmailSender.EmailCallback() {
                                                                        @Override
                                                                        public void onSuccess(String response) {
                                                                            runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Success: " + response, Toast.LENGTH_LONG).show());
                                                                        }

                                                                        @Override
                                                                        public void onFailure(String error) {
                                                                            runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Error: " + error, Toast.LENGTH_LONG).show());
                                                                        }
                                                                    });




                                                            Log.i("Electronic Shop", "User Successfully Registered");
                                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                                            firstName.setText("");
                                                            lastName.setText("");
                                                            email.setText("");
                                                            mobile.setText("");
                                                            password.setText("");
                                                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);

                                                            startActivity(i);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i("Electronic Shop", "User Registration Unsuccessful");
                                                            Toast.makeText(SignUpActivity.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.i("Electronic Shop", "Something went wrong");
                                        Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                    ;


                }
            }
        });


    }
}