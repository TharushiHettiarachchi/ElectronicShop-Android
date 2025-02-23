package lk.webstudio.elecshop.navigations;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;

public class AddProductFragment extends Fragment {

    private ActivityResultLauncher<Intent> galleryLauncher;
    private Cloudinary cloudinary;
    static Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);

        Spinner spinner = rootView.findViewById(R.id.spinner);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection("product_category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                QuerySnapshot querySnapshot = task.getResult();
                                String[] categories = new String[querySnapshot.size() + 1];
                                int i = 0;
                                categories[i] = "Select";
                                i = i + 1;
                                for (QueryDocumentSnapshot qs : querySnapshot) {
                                    categories[i] = qs.getString("category_name");
                                    i++;
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        categories
                                );
                                spinner.setAdapter(arrayAdapter);
                            } catch (Exception e) {
                                Log.i("ElecLog", String.valueOf(e));
                            }
                        }
                    }
                });

        ImageView imageView = rootView.findViewById(R.id.productImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    galleryLauncher.launch(intent);
                } catch (Exception e) {
                    Log.e("ElecLog", "Error opening gallery: " + e.getMessage());
                }
            }
        });

        Button button2 = rootView.findViewById(R.id.addProductBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText productName = rootView.findViewById(R.id.productName);
                EditText productCode = rootView.findViewById(R.id.productCode);
                EditText productQty = rootView.findViewById(R.id.ProductQtyTxt);
                EditText productPrice = rootView.findViewById(R.id.productPriceTxt);
                String productCategory = (String) spinner.getSelectedItem();

                Date date = new Date();

                if (selectedImageUri != null) {
                    try {

                        InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);


                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, len);
                        }
                        byte[] imageData = byteArrayOutputStream.toByteArray();


                        new Thread(() -> {
                            try {

                                Map<String, Object> uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
                                String imageUrl = (String) uploadResult.get("url");


                                getActivity().runOnUiThread(() -> {
                                    saveProductDataToFirestore(productName, productCode, productQty, productPrice, productCategory, date, imageUrl);
                                });
                            } catch (IOException e) {
                                getActivity().runOnUiThread(() -> Log.e("ElecLog", "Image upload failed: " + e.getMessage()));
                            }
                        }).start();

                    } catch (IOException e) {
                        Log.e("ElecLog", "Error opening InputStream: " + e.getMessage());
                    }
                }


            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        ImageView imageView = requireView().findViewById(R.id.productImageView);
                        imageView.setImageURI(selectedImageUri);
                    }
                }
        );

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dtguoyfdp",
                "api_key", "243595236562616",
                "api_secret", "GHYHeA8BUP7KAQzbp3UUe_XO-zA"));
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }


    private void saveProductDataToFirestore(EditText productName, EditText productCode, EditText productQty,
                                            EditText productPrice, String productCategory, Date date, String imageUrl) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("product_code", productCode.getText().toString());
        data.put("product_name", productName.getText().toString());
        data.put("date_added", String.valueOf(date));
        data.put("price", Integer.parseInt(productPrice.getText().toString()));
        data.put("product_category", productCategory);
        data.put("quantity", Integer.parseInt(productQty.getText().toString()));
        data.put("status", 1);
        data.put("user_id", MainActivity.userLogId);
        data.put("image_url", imageUrl);  // Save the image URL

        firestore.collection("products")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("ElecLog", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Product added Successfully", Toast.LENGTH_LONG).show();
                        productQty.setText("");
                        productCode.setText("");
                        productName.setText("");
                        productPrice.setText("");
                        ImageView imageView = requireView().findViewById(R.id.productImageView);
                        imageView.setImageResource(R.drawable.ic_menu_camera);
                        Spinner spinner = requireView().findViewById(R.id.spinner);
                        if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
                            spinner.setSelection(0);
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("ElecLog", "Error adding document: " + e.getMessage());
                    }
                });
    }


}
