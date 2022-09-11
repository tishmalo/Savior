package com.example.savior.Fragments;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savior.Adapter.NotificationAdapter;
import com.example.savior.Model.AudioModel;
import com.example.savior.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Notification extends Fragment {



    public Notification() {
        // Required empty public constructor
    }
    androidx.recyclerview.widget.RecyclerView rv;
    NotificationAdapter adapter;
    List<AudioModel> userList;
    DownloadManager mgr;
    long lastDownload=-1L;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_notification, container, false);

        rv=view.findViewById(R.id.recyclerview);

        userList=new ArrayList<>();
        adapter=new NotificationAdapter(getActivity(),userList);

        LinearLayoutManager lm=new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {


                DatabaseReference ref;
                ref=FirebaseDatabase.getInstance().getReference("Emergency");
                Query v=ref.orderByChild("myEmail").equalTo(FirebaseAuth.getInstance()
                        .getCurrentUser().getEmail());
                v.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot sn: snapshot.getChildren()){
                            final String EMAIL=sn.child("contactEmail").getValue().toString();

                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("audio");
                            Query query=reference.orderByChild("username").equalTo(EMAIL);

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot dt: snapshot.getChildren()){
                                        final String AUDIO=dt.child("audio").getValue().toString();
                                        mgr=(DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
                                        final String TimeStamp="" +System.currentTimeMillis();
                                        Uri uri=Uri.parse(AUDIO);

                                        Environment
                                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                                                .mkdirs();

                                        lastDownload=mgr
                                                .enqueue(new DownloadManager.Request(uri)
                                                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|
                                                                DownloadManager.Request.NETWORK_WIFI)
                                                        .setAllowedOverRoaming(false)
                                                        .setTitle("AUDIO")
                                                        .setDescription("AUDIo")
                                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,TimeStamp +
                                                                ".mp3"));


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }).attachToRecyclerView(rv);

        populateData();
        return view;
    }

    private void populateData() {

        DatabaseReference reference,ref;
        ref= FirebaseDatabase.getInstance().getReference("Emergency");
        reference=FirebaseDatabase.getInstance().getReference("audio");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    final String EMAIL=ds.child("contactEmail").getValue().toString();

                    Query v=reference.orderByChild("username").equalTo(EMAIL);
                    v.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userList.clear();
                            for (DataSnapshot sn:snapshot.getChildren()){
                                AudioModel a=sn.getValue(AudioModel.class);


                                userList.add(a);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}