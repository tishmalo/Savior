package com.example.savior.Fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.savior.Login;
import com.example.savior.R;
import com.example.savior.Register;
import com.example.savior.Services.RecordService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.InternalTokenProvider;

import java.util.HashMap;


public class Emergency extends Fragment  {

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    ImageView text;
    DatabaseReference ref;
    private LocationRequest locationRequest;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view=inflater.inflate(R.layout.fragment_emergency, container, false);

        text=view.findViewById(R.id.iconn);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServices();
                startLocationUpdates();
            }
        });

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                stopService();

                return true;
            }
        });

        return view;


    }



    private void stopService() {

        Glide.with(Emergency.this).load(R.drawable.elijah).into(text);
        Intent intent=new Intent(getActivity(),RecordService.class);
        getActivity().stopService(intent);

        Toast.makeText(getActivity(), "Stop Service", Toast.LENGTH_SHORT).show();

    }

    private void startServices() {

        Glide.with(Emergency.this).load(R.drawable.louis).into(text);

        Intent intent= new Intent(getActivity(), RecordService.class);
        getActivity().startService(intent);

    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    startLocationUpdates();

                } else {

                    turnOnGPS();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                startLocationUpdates();
            }
        }
    }

    private void startLocationUpdates() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    String CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    ref = FirebaseDatabase.getInstance().getReference()
                            .child("audio").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    LocationServices.getFusedLocationProviderClient(getActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(getActivity())
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        final String latitude = String.valueOf(locationResult.getLocations().get(index).getLatitude());
                                        final String longitude = String.valueOf(locationResult.getLocations().get(index).getLongitude());
                                        String currentLocation=String.valueOf(latitude)+", "+String.valueOf(longitude);

                                        HashMap userInfo = new HashMap();
                                        userInfo.put("id", CurrentUserId);
                                        userInfo.put("LatLong", currentLocation);



                                        ref.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(),"Location captured",Toast.LENGTH_SHORT);
                                                }
                                                else {
                                                    Toast.makeText(getActivity(),task.getException().toString(),Toast.LENGTH_SHORT);
                                                }

                                            }
                                        });

                                    }

                                }

                            }, Looper.getMainLooper());


                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


    }

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity().getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getActivity(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {

        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }



}