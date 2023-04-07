package com.example.meterreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private EditText userName;
    private  EditText newPass;

    private TextView forgotbtn;

    private TextView backtologin;

    private FirebaseAuth auth;
    CardView backButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.forgotemail);
        forgotbtn = findViewById(R.id.forgotbtn);
        userName = findViewById(R.id.userName);
        newPass =   findViewById(R.id.loginpassword);
        backButton = findViewById(R.id.back);

        auth = FirebaseAuth.getInstance();
        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void validateData() {
        String foremail = email.getText().toString().trim();
        String forUser = userName.getText().toString().trim();
        String newPassword = newPass.getText().toString().trim();

        if (TextUtils.isEmpty(foremail) || TextUtils.isEmpty(foremail) || TextUtils.isEmpty(newPassword)){
            Toast.makeText(this, "Enter The Data", Toast.LENGTH_SHORT).show();
        }
        else {
            passMail(foremail,forUser,newPassword);

        }
    }
    private  void passMail(String email1,String user, String pass){
        auth.sendPasswordResetEmail(email1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "set Mail", Toast.LENGTH_SHORT).show();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
                            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // check username and password exist in database
                                    if(snapshot.hasChild(user)){
                                        // password exist
                                        final String getEmail = snapshot.child(user).child("email").getValue(String.class);
//                                        final String getPassword = snapshot.child(txt_user).child("password").getValue().toString();
                                        if(getEmail.equals(email1)){

                                            // Update Password
                                            databaseReference.child("users").child(user).child("password").setValue(pass);
                                            Toast.makeText(ForgotPassword.this, "Password was Change Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ForgotPassword.this,MainActivity.class));
                                            finish();

                                        }
                                        else{
                                            Toast.makeText(ForgotPassword.this, "Email Address Not Exist", Toast.LENGTH_SHORT).show();
                                            email.setText("");
                                        }
                                    }
                                    else{
                                        Toast.makeText(ForgotPassword.this, "User Does Not Exist" , Toast.LENGTH_SHORT).show();
                                        userName.setText("");
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
    }
}