package com.example.meterreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class billLayout extends AppCompatActivity {

    TextView currReading;
    TextView lastReading;

    TextView currDate;
    FirebaseDatabase database;
    DatabaseReference references;
    ImageView backPage;
    FirebaseAuth mauth;

    TextView total;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_layout);

        Intent i = getIntent();
        String currR = i.getStringExtra("currentReading").toString();
        String activity = i.getStringExtra("activity").toString();


        currReading = findViewById(R.id.currReading);
        lastReading = findViewById(R.id.lastReading);
        currDate = findViewById(R.id.date);
        backPage = findViewById(R.id.backPage);
        total = findViewById(R.id.total);


        currReading.setText(currR);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        currDate.setText(dateFormat.format(date));

        mauth = FirebaseAuth.getInstance();
        String userId = mauth.getCurrentUser().getUid().toString().trim();

        database = FirebaseDatabase.getInstance();
        references = database.getReference("users");


//        Access Last Reading if Exist
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild(userId)){
                    if(TextUtils.equals(activity,"Electricity")){
                        if(snapshot.child(userId).hasChild("Your Last Electricity Reading")){
                            Toast.makeText(billLayout.this, "vs", Toast.LENGTH_SHORT).show();
                            lastReading.setText(snapshot.child(userId).child("Your Last Electricity Reading").getValue().toString());
                            int a = Integer.parseInt(lastReading.getText().toString()) - Integer.parseInt(currReading.getText().toString());
                            total.setText(String.valueOf(Math.abs(a)));
                        }
                        else{
                            lastReading.setText("0");
                        }
                    }
                    else{
                        if(snapshot.child(userId).hasChild("Your Last Water Reading")){
                            lastReading.setText(snapshot.child(userId).child("Your Last Water Reading").getValue().toString());
                        }
                        else{
                            lastReading.setText("0");
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Update Current Reading in DataBase

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(billLayout.this,selectionOfEle.class);

                if(TextUtils.equals(activity,"Electricity")){
                    references.child(userId).child("Your Last Electricity Reading").setValue(currR).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(billLayout.this, "Data Updation", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(billLayout.this, "Data Updation Faild", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    references.child(userId).child("Your Last Water Reading").setValue(currR).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(billLayout.this, "Data Updation", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(billLayout.this, "Data Updation Faild", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }
}