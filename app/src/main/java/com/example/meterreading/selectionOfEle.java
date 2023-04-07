package com.example.meterreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class selectionOfEle extends AppCompatActivity {

    private Button logout;
    FirebaseAuth auth;

    DrawerLayout drawerLayout;

    NavigationView navigationView;

    MaterialToolbar appBar;
    TextView drawIcon;
    TextView drawText;
    ActionBarDrawerToggle actionBarDrawerToggle;

    CardView EleBill;
    CardView waterBill;
    TextView userCardName;
    TextView userCardEmail;
    NotificationManager nm;
    Notification notification;
    private static final String CHENALL_ID = "Logout";
    private static final int NOTIFICATION_ID = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_of_ele);


        EleBill = findViewById(R.id.EleBill);
        waterBill = findViewById(R.id.waterBill);
        userCardName = findViewById(R.id.userCardName);
        userCardEmail = findViewById(R.id.userCardEmail);


        // Action Bar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView  = findViewById(R.id.nav_view);
        appBar = findViewById(R.id.appBar);
        drawIcon = findViewById(R.id.drawicon);
        drawText = findViewById(R.id.drawText);
        appBar.setTitle("MeterReading");

        setupView();

        View header = navigationView.getHeaderView(0);
        drawText = header.findViewById(R.id.drawText);
        drawIcon = header.findViewById(R.id.drawicon);



//      Notification

        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.appicon,null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;

        Bitmap largeIcon = bitmapDrawable.getBitmap();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentText("Logout Successfull")
//                    .setSubText("MeterReading")
                    .setChannelId(CHENALL_ID)
                    .build();

            nm.createNotificationChannel(new NotificationChannel(CHENALL_ID,"Login" ,NotificationManager.IMPORTANCE_HIGH));
        }
        else{
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentText("Login Successfull")
//                    .setSubText("MeterReading")
                    .build();
        }



        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid().toString().trim();
        final String[] userName = {" "};

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild(userId)){

                    if (!snapshot.child(userId).hasChild("name")){
                        Dialog dialog = new Dialog(selectionOfEle.this);
                        dialog.setContentView(R.layout.custome_dialog_layout);
                        dialog.setCancelable(false);
                        TextView button = dialog.findViewById(R.id.btnOkay);
                        EditText dialogUserName = dialog.findViewById(R.id.dialogUserName);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();

                                String dialogUser = dialogUserName.getText().toString().trim();
                                if (TextUtils.isEmpty(dialogUser)){
                                    Toast.makeText(selectionOfEle.this, "Please Enter the Details", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    userName[0] = dialogUser;
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference references = database.getReference("users");
                                    references.child(userId).child("name").setValue(dialogUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            userCardName.setText(dialogUser);
                                            drawText.setText("" + dialogUser);
                                            drawIcon.setText("" + dialogUser.toUpperCase().charAt(0));
                                            Toast.makeText(selectionOfEle.this, "Updation SuccessFully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Toast.makeText(selectionOfEle.this, dialogUser, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.show();
                    }
                    else{
                        userName[0] = snapshot.child(userId).child("name").getValue().toString().trim();
                    }
                    String show  = userName[0].toUpperCase();
                    drawText.setText(""+userName[0]);
                    drawIcon.setText(""+show.charAt(0));
//                    appBar.setSubtitle("Hello");
                    userCardName.setText(userName[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String email = auth.getCurrentUser().getEmail().toString().trim();
//        userCardEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Profile:
                        Intent intent = new Intent(selectionOfEle.this,Profile.class);
                        intent.putExtra("user_Name", userName[0]);
                        intent.putExtra("Email",email);
                        startActivity(intent);
                        return true;

                    case R.id.Contactus:
                        Toast.makeText(selectionOfEle.this, "Contact us", Toast.LENGTH_SHORT).show();
                        return true;


                    case R.id.About:
//                        Toast.makeText(selectioOfEle.this, "About us", Toast.LENGTH_SHORT).show();
                        intent = new Intent(selectionOfEle.this,Aboutus.class);
                        startActivity(intent);
                        return true;

                    case R.id.logout:
                        auth.signOut();
                        nm.notify(NOTIFICATION_ID,notification);
//                Toast.makeText(selectionOfEle.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(selectionOfEle.this,Login.class));
                        finish();

                }
                return true;
            }
        });



        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                nm.notify(NOTIFICATION_ID,notification);
//                Toast.makeText(selectionOfEle.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(selectionOfEle.this,Login.class));
                finish();
            }
        });


        // Both Button
        EleBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selectionOfEle.this,electicityBill.class);
                startActivity(intent);
            }
        });

        waterBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selectionOfEle.this,waterBill.class);
                startActivity(intent);
            }
        });


    }

    // Action Bar
    private void setupView() {
        setupDrawerLayout();
    }

    private void setupDrawerLayout() {
        setSupportActionBar(appBar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.menu_open,R.string.menu_close);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}