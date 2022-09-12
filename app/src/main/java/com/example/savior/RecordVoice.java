package com.example.savior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savior.Model.AudioModel;
import com.google.android.gms.tasks.Continuation;
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

public  class RecordVoice extends AppCompatActivity implements View.OnClickListener {

    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;


    public RecordVoice() {

        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
        // Required empty public constructor
    }

    public RecordVoice(long doubleClickQualificationSpanInMillis) {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }


    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static String path = null;

   TextView record, stop,play;
    private MediaRecorder recorder = null;

    private static final int REQUEST_CODE = 1;
    private MediaPlayer player = null;


    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        29);


            } else {
                Log.d("Home", "Already granted access");




                fileName = getExternalCacheDir().getAbsolutePath();
                fileName += "audiorecordtest.3gp";

                record = findViewById(R.id.record);
                play = findViewById(R.id.play);
                stop = findViewById(R.id.stop);


                recording();
                playing();
                stopping();
            }
        }

    }

    private void playing() {

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                player= new MediaPlayer();
                try {
                    player.setDataSource(fileName);
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                play.setText("playing");

            }
        });
    }

    private void stopping() {

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.stop();
                recorder.release();
                recorder=null;

                stop.setText("stopped");

                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                final String TimeStamp="" +System.currentTimeMillis();

                StorageMetadata metadata=new StorageMetadata.Builder()
                        .setContentType("audio/mpeg")
                        .build();

                StorageReference file=storageReference.child(TimeStamp+
                        ".mp3");

                Uri uri=Uri.fromFile(new File(fileName));

                UploadTask uploadTask=file.putFile(uri,metadata);



                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(RecordVoice.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("audio");
                                    final String AUDIO=uri.toString();

                                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getUid());

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            final String USERNAME=snapshot.child("email").getValue().toString();
                                            final String LatLong="";

                                            AudioModel am=new AudioModel(USERNAME,AUDIO,LatLong);

                                            ref.push().setValue(am);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });




                                }
                            });

                        }else{
                            Toast.makeText(RecordVoice.this, "Upload failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }

    private void recording() {

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recorder= new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                recorder.setAudioSamplingRate(96000);
                recorder.setAudioEncodingBitRate(128000);
                recorder.setOutputFile(fileName);


                try {
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                recorder.start();
                record.setText("recording");



            }
        });
    }


    @Override
    public void onClick(View view) {

        if((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis) {
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();


    }

    private void onDoubleClick() {

        Toast.makeText(getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();
    }
}