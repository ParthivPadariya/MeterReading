package com.example.meterreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class Profile extends AppCompatActivity {

    TextView userName;
    TextView Email;
    ImageView back;
    Button updateP;

    EditText PhoneNumber;
    FirebaseDatabase database;
    DatabaseReference references;
    FirebaseAuth mauth;
//    AutoCompleteTextView select_Bord;
//    AutoCompleteTextView selectState;
//    String[] eleBord = {"Paschim Gujarat Vij Company Limited(PGVCL)","Dakshin Gujarat Vij Company Limited","Gift Power Company Limited", "Madhya Gujarat Vij Company Limited", "Torrent Power Limited", "Uttar Gujarat Vij Company Limited"};
//    ArrayAdapter<String> bordAdaperter ;
//    ArrayAdapter<CharSequence> stateAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName = findViewById(R.id.userName);
        Email = findViewById(R.id.emailId);
        updateP = findViewById(R.id.updateProfile);
        PhoneNumber = findViewById(R.id.userphone);
        back = findViewById(R.id.back);

        mauth = FirebaseAuth.getInstance();
        String userId = mauth.getCurrentUser().getUid().toString().trim();
//        ArrayList<String> EleBord = new ArrayList<>();
//        EleBord.addAll(Arrays.asList(eleBord));
/*

        selectState = findViewById(R.id.selectState);
        stateAdapter = ArrayAdapter.createFromResource(this,R.array.array_indian_state,R.layout.list_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectState.setAdapter(stateAdapter);
        selectState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                selectState.setText(item);
                Toast.makeText(Profile.this, item, Toast.LENGTH_SHORT).show();
            }
        });


        select_Bord = findViewById(R.id.selectBord);
        bordAdaperter = new ArrayAdapter<String>(this,R.layout.list_item,eleBord);
        select_Bord.setAdapter(bordAdaperter);
        select_Bord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Profile.this, item, Toast.LENGTH_SHORT).show();
            }
        });
*/


        String txt_userName = getIntent().getStringExtra("user_Name").toString();
        String txt_email = getIntent().getStringExtra("Email").toString();
        userName.setText(txt_userName);
        Email.setText(txt_email);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this,selectionOfEle.class);
//                intent.putExtra("userName",txt_userName);
//                intent.putExtra("Email",txt_email);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        references = database.getReference("users");
//        String phone_number = PhoneNumber.getText().toString();
        // Add Number
//        PhoneNumber.setText(PhoneNumber.getText().toString());
        // Update user Profile
//        references.child(txt_userName


        // Retrive Phone Number if Exist and set in Phonenumber
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild(userId)){
                    if(snapshot.child(userId).hasChild("phoneNumber")){
                        PhoneNumber.setText(snapshot.child(userId).child("phoneNumber").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Update
        updateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PhoneNumber.getText().toString().length()  <=  13 && PhoneNumber.getText().toString().length()  >=  10){

                    references.child(userId).child("phoneNumber").setValue(PhoneNumber.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            PhoneNumber.setText(PhoneNumber.getText().toString().trim());
                            Toast.makeText(Profile.this, "Profile Update", Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this, "Profile Updation Faild", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(Profile.this, "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}