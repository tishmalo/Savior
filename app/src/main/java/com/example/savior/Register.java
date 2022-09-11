package com.example.savior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity  {

    private EditText email, password, cpassword;
    private Button submit;
    TextView already;
    ProgressDialog loader2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        cpassword=findViewById(R.id.cpassword);
        submit=findViewById(R.id.submit);
        already=findViewById(R.id.already);

        loader2= new ProgressDialog(this);

        email.setTextColor(Color.WHITE);
        password.setTextColor(Color.WHITE);
        cpassword.setTextColor(Color.WHITE);

        signinuser();
        gotologin();


    }

    private void gotologin() {
        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(getApplicationContext(), Login.class);
                startActivity(intent1);
            }
        });
    }

    private void signinuser() {


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String EMAIL = email.getText().toString();
                final String PASSWORD = password.getText().toString();
                final String CPASSWORD = cpassword.getText().toString();


                if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()) {
                    email.setError("Invalid email");
                    return;
                }if(TextUtils.isEmpty(PASSWORD)){
                    password.setError("ENTER PASSWORD");
                    return;
                }if(TextUtils.isEmpty(CPASSWORD)){
                    cpassword.setError("Enter password");
                    return;
                }
                if(!PASSWORD.equals(CPASSWORD)){
                    cpassword.setError("passwords must match");
                    return;

                }if(PASSWORD.length()<6){
                    password.setError("weak Password");
                    return;
                }else {

                    loader2.setMessage("Registering you");
                    loader2.setCanceledOnTouchOutside(false);
                    loader2.show();

                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


                                DatabaseReference ref;
                                ref = FirebaseDatabase.getInstance().getReference("users");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        HashMap map = new HashMap();
                                        map.put("email", EMAIL);
                                        map.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);

                                        finish();



                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                loader2.dismiss();
                                finish();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);




                            } else {
                                Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



                }
            }
        });







        }


    }

            