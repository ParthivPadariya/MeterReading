package com.example.meterreading;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignupPage extends AppCompatActivity {


    private EditText email;
    private EditText password;
    private EditText password2;
    private TextView signup;
    private EditText userName;


    FirebaseAuth mauth;

    FirebaseDatabase database;
    DatabaseReference references;
    CardView backButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        signup = findViewById(R.id.signupbtn);
        userName = findViewById(R.id.userName);
        backButton = findViewById(R.id.back);


        mauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        references = database.getReference("users");
/*
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useName_txt = userName.getText().toString();
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();
                String password_txt2 = password2.getText().toString();

                if(!password_txt.equals(password_txt2)){
                    Toast.makeText(SignupPage.this, "Please Enter valid Password", Toast.LENGTH_SHORT).show();
                    password2.setText("");
                }
                else if(TextUtils.isEmpty(useName_txt) || TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) || TextUtils.isEmpty(password_txt2)){
                    Toast.makeText(SignupPage.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Add Data in Data Base
                    Helper helper = new Helper(useName_txt, email_txt,password_txt);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // if User Already Exists (Same userName Not Alowed)
                            if(snapshot.hasChild(useName_txt)){
                                Toast.makeText(SignupPage.this, "inValid UserName", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // User Not Exist then make a user
                                references.child(useName_txt).setValue(helper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignupPage.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupPage.this, MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignupPage.this, "Register Faild", Toast.LENGTH_SHORT).show();
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
        });
*/

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupPage.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString().trim();
                String txt_pass = password.getText().toString().trim();
                String txt_uname = userName.getText().toString().trim();
                String txt_pass2 = password2.getText().toString().trim();

                if(!txt_pass.equals(txt_pass2)){
                    Toast.makeText(SignupPage.this, "Please Enter valid Password", Toast.LENGTH_SHORT).show();
                    password2.setText("");
                }
                else if(TextUtils.isEmpty(txt_uname) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass) || TextUtils.isEmpty(txt_pass2)){
                    Toast.makeText(SignupPage.this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
                }
                else{
                    createAccount(txt_email,txt_pass,txt_uname);
                }
            }
        });

    }

    private void createAccount(String email, String password,String userName) {

        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            addInRealTime(email,password,userName);
                            mauth.signOut();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]

    }

    private void addInRealTime(String email, String password,String userName){
        String userId = mauth.getCurrentUser().getUid().toString().trim();
        Helper helper = new Helper(userName, email,password,userId);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if User Already Exists (Same userName Not Alowed)
                if(snapshot.hasChild(userId)){
                    Toast.makeText(SignupPage.this, "inValid UserName", Toast.LENGTH_SHORT).show();
                }
                else{
                    // User Not Exist then make a user
                    references.child(userId).setValue(helper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SignupPage.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupPage.this,Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupPage.this, "Register Faild", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    /*
    private  void  registerInRealTime(String email, String password, String txt_userName){
        String userid = auth.getCurrentUser().getUid().toString();
        Helper helper = new Helper(txt_userName, email,password);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // if User Already Exists (Same userName Not Alowed)
                if(snapshot.hasChild(userid)){
                    Toast.makeText(SignupPage.this, "inValid UserName", Toast.LENGTH_SHORT).show();
                }
                else{
                    // User Not Exist then make a user
                    references.child(userid).setValue(helper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(SignupPage.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupPage.this, MainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupPage.this, "Register Faild", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
*/

}