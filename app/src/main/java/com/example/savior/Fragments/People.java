package com.example.savior.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.savior.Adapter.UserAdapter;
import com.example.savior.Model.ContactModel;
import com.example.savior.Model.UserModel;
import com.example.savior.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class People extends Fragment {


    public People() {
        // Required empty public constructor
    }
    List<UserModel> userModelList;
    UserAdapter adapter;
    androidx.recyclerview.widget.RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_people, container, false);

        recyclerView=v.findViewById(R.id.recyclerview);


        userModelList=new ArrayList<>();
        adapter=new UserAdapter(getActivity(),userModelList);

        LinearLayoutManager lm=new LinearLayoutManager(getActivity());
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        populateData();

        swipetoAdd();

        return v;
    }

    private void swipetoAdd() {

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                String addItem=userModelList.get(viewHolder.getAdapterPosition()).getemail();

                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Emergency");

                ContactModel cm=new ContactModel(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        addItem,FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(cm);

                userModelList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);


    }

    private void populateData() {

        DatabaseReference ref;
        ref= FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModelList.clear();
                for (DataSnapshot sn:snapshot.getChildren()){
                   UserModel um=sn.getValue(UserModel.class);
                   userModelList.add(um);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}