package com.subairdc.bdma.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.subairdc.bdma.R;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView bannar, registerUser;
    private EditText editTextfullName, editTextPhoneNo, editTextEmail, editTextPassword, editTextConformPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        bannar = (TextView) findViewById(R.id.bannar);
        bannar.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextfullName = (EditText) findViewById(R.id.fullName);
        editTextPhoneNo = (EditText) findViewById(R.id.phoneNo);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextConformPassword = (EditText) findViewById(R.id.conformPassword);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bannar:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String fullName = editTextfullName.getText().toString().trim();
        String phoneNo = editTextPhoneNo.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String conformPassword = editTextConformPassword.getText().toString().trim();

        if(fullName.isEmpty()){
            editTextfullName.setError("Full Name is required");
            editTextfullName.requestFocus();
            return;
        }

        if(phoneNo.isEmpty()){
            editTextPhoneNo.setError("Phone Number is required");
            editTextPhoneNo.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("email is required");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid Email");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Min Password length should 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

       /* if(password!=conformPassword) {
            editTextConformPassword.setError("Password Doesn't Match");
            editTextConformPassword.requestFocus();
            return;
        */

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            User user = new User(fullName, phoneNo, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "User has been registered successfull", Toast.LENGTH_LONG).show();
                                        //Redirect to login layout
                                    }else {
                                        Toast.makeText(RegisterUser.this, "Failed to register! Pls Try Again", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }else {
                            Toast.makeText(RegisterUser.this, "Failed to register Pls Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        }

    }