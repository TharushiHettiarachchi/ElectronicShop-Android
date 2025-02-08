package lk.webstudio.elecshop;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class MainActivity2 extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 11010;  // Define the request code
    private TextView textView;  // Declare the TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        // Ensure 'main' layout exists
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextView
        textView = findViewById(R.id.buttonView);  // Ensure this TextView exists in XML

        Button orderBtn = findViewById(R.id.order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitRequest req = new InitRequest();
                req.setMerchantId("1221108 ");

                // Merchant ID
                req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                req.setAmount(1000.00);             // Final Amount to be charged
                req.setOrderId("230000123");        // Unique Reference ID
                req.setItemsDescription("Door bell wireless");  // Item description title
                req.setCustom1("This is the custom message 1");
                req.setCustom2("This is the custom message 2");
                req.getCustomer().setFirstName("Saman");
                req.getCustomer().setLastName("Perera");
                req.getCustomer().setEmail("samanp@gmail.com");
                req.getCustomer().setPhone("+94771234567");
                req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                req.getCustomer().getAddress().setCity("Colombo");
                req.getCustomer().getAddress().setCountry("Sri Lanka");

                Intent intent = new Intent(MainActivity2.this, PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                startActivityForResult(intent, PAYHERE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            if (resultCode == Activity.RESULT_OK) {
                if (response != null && response.isSuccess()) {
                    String msg = "Activity result: " + response.getData().toString();
                    Log.d(TAG, msg);
                    Log.i("ElecLog",msg);
                    textView.setText(msg);
                } else {
                    String msg = "Result: " + (response != null ? response.toString() : "no response");
                    Log.d(TAG, msg);
                    Log.i("ElecLog",msg);
                    textView.setText(msg);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                textView.setText(response != null ? response.toString() : "User canceled the request");
                Log.i("ElecLog",response.toString());
            }
        }
    }
}
