package lk.webstudio.elecshop.navigations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.webstudio.elecshop.BroadcastCheck;
import lk.webstudio.elecshop.HomeActivity;
import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;

public class ProfileFragment extends Fragment {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLatLng; // Class-level variable to store the current location

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        BroadcastCheck broadcastCheck = new BroadcastCheck();
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        rootView.getContext().registerReceiver(broadcastCheck, intentFilter);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled || isNetworkEnabled) {
            // Location is ON
            Log.i("ElecLog", "Location ON");
            EditText fname = rootView.findViewById(R.id.profileFname);
            EditText lname = rootView.findViewById(R.id.profileLname);
            EditText mobile = rootView.findViewById(R.id.profileMobile);
            EditText email = rootView.findViewById(R.id.profileEmail);
            EditText password = rootView.findViewById(R.id.profilePassword);
            TextView profileDate = rootView.findViewById(R.id.profileRegisteredDate);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("user")
                    .whereEqualTo(FieldPath.documentId(), MainActivity.userLogId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    for (QueryDocumentSnapshot qs : querySnapshot) {
                                        fname.setText(qs.getString("firstName"));
                                        lname.setText(qs.getString("lastName"));
                                        email.setText(qs.getString("email"));
                                        password.setText(qs.getString("password"));
                                        mobile.setText(qs.getString("mobile"));
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        String formattedDate = sdf.format(qs.getDate("registered_on"));
                                        profileDate.setText("Registered On " + formattedDate);


                                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());


                                        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapLayout);
                                        if (mapFragment == null) {
                                            mapFragment = new SupportMapFragment();
                                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.mapLayout, mapFragment).commit();
                                        }

                                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                                Log.i("ElecLog", "Map Ready");
                                                mMap = googleMap;


                                                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                    mMap.setMyLocationEnabled(true);
                                                    Map<String, Object> locationMap = (Map<String, Object>) qs.get("location");
                                                    if (locationMap != null) {
                                                        double latitude = (double) locationMap.get("latitude");
                                                        double longitude = (double) locationMap.get("longitude");
                                                        currentLatLng = new LatLng(latitude, longitude);

                                                        // Move the camera to current location
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                                                        // Add a draggable marker
                                                        Marker marker = mMap.addMarker(
                                                                new MarkerOptions()
                                                                        .position(currentLatLng)
                                                                        .title("Me")
                                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))
                                                                        .draggable(true) // Allow dragging
                                                        );

                                                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                                            @Override
                                                            public void onMarkerDrag(@NonNull Marker marker) {
                                                                // Optional: Provide real-time feedback during dragging
                                                            }

                                                            @Override
                                                            public void onMarkerDragEnd(@NonNull Marker marker) {
                                                                // Update currentLatLng when marker is dropped
                                                                currentLatLng = marker.getPosition();
                                                                Log.i("ElecLog", "Marker moved to: " + currentLatLng.latitude + ", " + currentLatLng.longitude);
                                                            }

                                                            @Override
                                                            public void onMarkerDragStart(@NonNull Marker marker) {
                                                                // Optional: Log or provide UI feedback when dragging starts
                                                            }
                                                        });

                                                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                            @Override
                                                            public void onMapClick(@NonNull LatLng latLng) {
                                                                mMap.clear();
                                                                mMap.addMarker(
                                                                        new MarkerOptions()
                                                                                .position(currentLatLng)
                                                                                .title("Me")
                                                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))
                                                                                .draggable(true) // Allow dragging
                                                                );
                                                                currentLatLng = latLng;
                                                            }
                                                        });

                                                    }else{
                                                        getCurrentLocation();
                                                    }



                                                } else {
                                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                                }
                                            }
                                        });


                                    }

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });




            Button saveLocationBtn = rootView.findViewById(R.id.saveLocBtn);
            saveLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    firestore
                            .collection("user")
                            .document(MainActivity.userLogId)
                            .update("location", currentLatLng)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(requireContext(), "Succesfully saved", Toast.LENGTH_LONG).show();
                                }
                            });


                }
            });

            Button updateBtn = rootView.findViewById(R.id.updateBtn);
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fname.getText().toString().isEmpty()) {
                        Toast.makeText(rootView.getContext(), "Please Enter the First Name", Toast.LENGTH_LONG).show();
                    } else if (lname.getText().toString().isEmpty()) {
                        Toast.makeText(rootView.getContext(), "Please Enter the Last Name", Toast.LENGTH_LONG).show();
                    } else if (mobile.getText().toString().isEmpty()) {
                        Toast.makeText(rootView.getContext(), "Please Enter the Mobile Number", Toast.LENGTH_LONG).show();
                    } else if (password.getText().toString().isEmpty()) {
                        Toast.makeText(rootView.getContext(), "Please Enter the Password", Toast.LENGTH_LONG).show();
                    } else {
                        HashMap<String, Object> updatelis = new HashMap<>();
                        updatelis.put("mobile", mobile.getText().toString());
                        updatelis.put("firstName", fname.getText().toString());
                        updatelis.put("lastName", lname.getText().toString());
                        updatelis.put("password", password.getText().toString());

                        FirebaseFirestore firestore1 = FirebaseFirestore.getInstance();
                        firestore1
                                .collection("user")
                                .document(MainActivity.userLogId)
                                .update(updatelis)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(rootView.getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }


                }
            });


        } else {
            // Location is OFF
            Log.i("ElecLog", "Location OFF");
            Toast.makeText(requireContext(), "Please Turn on Location", Toast.LENGTH_LONG).show();
            Intent i = new Intent(requireContext(), HomeActivity.class);
            startActivity(i);
        }


        return rootView;
    }

    // Get and set the current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Move the camera to current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                    // Add a draggable marker
                    Marker marker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("Me")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))
                                    .draggable(true) // Allow dragging
                    );

                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDrag(@NonNull Marker marker) {
                            // Optional: Provide real-time feedback during dragging
                        }

                        @Override
                        public void onMarkerDragEnd(@NonNull Marker marker) {
                            // Update currentLatLng when marker is dropped
                            currentLatLng = marker.getPosition();
                            Log.i("ElecLog", "Marker moved to: " + currentLatLng.latitude + ", " + currentLatLng.longitude);
                        }

                        @Override
                        public void onMarkerDragStart(@NonNull Marker marker) {
                            // Optional: Log or provide UI feedback when dragging starts
                        }
                    });

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng latLng) {
                            mMap.clear();
                            mMap.addMarker(
                                    new MarkerOptions()
                                            .position(currentLatLng)
                                            .title("Me")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.myloc))
                                            .draggable(true) // Allow dragging
                            );
                            currentLatLng = latLng;
                        }
                    });


                } else {
                    Log.e("ElecLog", "Failed to get current location");
                }
            }
        });
    }

    // Handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            }
        }
    }
}
