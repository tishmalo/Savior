package com.example.savior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText email,password;
    private Button login;
    private TextView already;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        already=findViewById(R.id.already);


        email.setTextColor(Color.WHITE);
        password.setTextColor(Color.WHITE);

        auth=FirebaseAuth.getInstance();

        gotosignup();
        ListenforRegistereduser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String EMAIL= email.getText().toString();
                final String PASSWORD= password.getText().toString();


                if(!EMAIL.isEmpty()){
                    if(!PASSWORD.isEmpty()){

                        FirebaseAuth mAuth;
                        mAuth=FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(EMAIL,PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Intent intent= new Intent(Login.this, HomeActivity.class);
                                    startActivity(intent);
                                }

                            }
                        });
                    }
                }

            }
        });


    }

    private void ListenforRegistereduser() {
        
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user;
                user= FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null){

                    Intent intent2= new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent2);

                    finish();
                }
                
            }
        };
        
        
    }

    private void gotosignup() {

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(Login.this, Register.class);
                startActivity(intent2   );

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();


        auth.addAuthStateListener(authStateListener);


    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(authStateListener);

    }
}