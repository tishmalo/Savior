package com.example.savior.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.savior.Model.AudioModel;
import com.example.savior.RecordVoice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class RecordService extends Service {


    private Timer timer;
    MediaPlayer player;
    String flag = "0";


    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static String path = null;


    private MediaRecorder recorder = null;

    private static final int REQUEST_CODE = 1;


    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        recorder = new MediaRecorder();


        doSomethingRepeatedly();



        return START_STICKY;
    }

    private void doSomethingRepeatedly() {


        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
             new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override
                 public void run() {
                     if(flag.equals("1")){
                         stop();
                     }else {
                         try {


                             // player=MediaPlayer.create(getApplicationContext(),  Settings.System.DEFAULT_RINGTONE_URI);

                             //player.setLooping(true);
                             // player.start();
                             fileName = getExternalCacheDir().getAbsolutePath();
                             fileName += "audiorecordtest.3gp";

                             recorder = new MediaRecorder();
                             recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                             recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                             recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                             recorder.setAudioSamplingRate(96000);
                             recorder.setAudioEncodingBitRate(128000);
                             recorder.setOutputFile(fileName);

                             try {
                                 recorder.prepare();
                                 recorder.start();
                                 flag="1";

                             } catch (IOException e) {
                                 e.printStackTrace();
                             }




                         } catch (Exception e) {
                             // TODO: handle exception
                         }
                     }


                 }
             });
           }
       };
       timer.scheduleAtFixedRate(task,0,18000);




    }

    private void stop() {


        recorder.stop();
        recorder.release();
        recorder = null;







            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String TimeStamp = "" + System.currentTimeMillis();

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("audio/mpeg")
                    .build();

            StorageReference file = storageReference.child("tmp3").child(TimeStamp +
                    ".mp3");

            Uri uri = Uri.fromFile(new File(fileName));

            UploadTask uploadTask = file.putFile(uri, metadata);


            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(RecordService.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("audio");
                                final String AUDIO = uri.toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        final String USERNAME = snapshot.child("email").getValue().toString();

                                        AudioModel am = new AudioModel(USERNAME, AUDIO);

                                        ref.push().setValue(am);


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        });

                    } else {
                        Toast.makeText(RecordService.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });



    }



    @Override
    public void onDestroy() {

        timer.cancel();
    }
}
