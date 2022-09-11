package com.example.savior.Adapter;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.savior.Model.AudioModel;
import com.example.savior.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<AudioModel> userList;
    DownloadManager mgr=null;
    long lastDownload=-1L;

    public NotificationAdapter(Context context, List<AudioModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.show_recording,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        AudioModel am=userList.get(position);
        holder.EMAIL.setText(am.getusername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref;
                ref= FirebaseDatabase.getInstance().getReference("audio");
                Query v=ref.orderByChild("username").equalTo(am.getusername());
                v.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot sn: snapshot.getChildren()){

                            final String AUDIO=sn.child("audio").getValue().toString();


                            MediaPlayer mp=new MediaPlayer();
                            mp.setAudioAttributes(
                                    new AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build()
                            );
                            try {
                                mp.setDataSource(context, Uri.parse(AUDIO));
                                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                        mp.start();
                                        Toast.makeText(context, "Audio playing", Toast.LENGTH_LONG).show();
                                    }
                                });

                                mp.prepare();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {


                    }
                });

            }
        });



    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView EMAIL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            EMAIL=itemView.findViewById(R.id.reg);

        }
    }
}
