package com.example.savior.Fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.internal.InternalTokenProvider;


public class Emergency extends Fragment  {

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;
    ImageView text;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view=inflater.inflate(R.layout.fragment_emergency, container, false);

        text=view.findViewById(R.id.iconn);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServices();
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



}