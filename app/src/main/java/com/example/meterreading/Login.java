package com.example.meterreading;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Login extends AppCompatActivity {
    boolean passVisible;
    private EditText user;
    private EditText password;
    public TextView register;
    public TextView login;
    public TextView forgotPass;

    private FirebaseAuth auth;

    private GoogleSignInClient client;
    private TextView login_google;
    GoogleSignInClient mGoogleSignInClient;

    GoogleSignInAccount account;
    ProgressDialog progressDialog;
    private static final String CHENALL_ID = "Login";
    private static final int NOTIFICATION_ID = 100;

    NotificationManager nm;
    Notification notification;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Google Sign In...");


        login = findViewById(R.id.loginbtn1);
        user = findViewById(R.id.userName);
        password = findViewById(R.id.loginpassword);
        forgotPass = findViewById(R.id.forgotPass);


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this,selectionOfEle.class);
//            intent.putExtra("userName",uname);
//            intent.putExtra("Email",auth.getCurrentUser().getEmail().toString().trim());
            startActivity(intent);
            finish();
        }

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,ForgotPassword.class);
                startActivity(intent);
            }
        });

//        Notification

//        get Drawable icon
        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.appicon,null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;

        Bitmap largeIcon = bitmapDrawable.getBitmap();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentText("Login Successfull")
//                    .setSubText("MeterReading")
                    .setChannelId(CHENALL_ID)
                    .build();

            nm.createNotificationChannel(new NotificationChannel(CHENALL_ID,"New Chennal" ,NotificationManager.IMPORTANCE_HIGH));
        }
        else{
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.appicon)
                    .setContentText("Login Successfull")
//                    .setSubText("MeterReading")
                    .build();
        }




//  Google Login
/*
        login_google = (TextView) findViewById(R.id.loginbtn2);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(MainActivity.this,options);
        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = client.getSignInIntent();
                startActivityForResult(i,123);
            }
        });
*/
        login_google = findViewById(R.id.loginbtn2);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                progressDialog.show();
                startActivityForResult(signInIntent,123);
            }
        });

//

        register = (TextView) findViewById(R.id.loginbtn3);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,SignupPage.class));
            }
        });




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_user = user.getText().toString().trim();
                String txt_password = password.getText().toString().trim();
                if(TextUtils.isEmpty(txt_user) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(Login.this, "Please Valid Credential", Toast.LENGTH_SHORT).show();
                }
                else{
//                    loginUser(txt_user,txt_password);
                    signIn(txt_user,txt_password);
                }
            }
        });

    }

/*
    private void loginUser(String txt_user, String txt_password) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check username and password exist in database
                if(snapshot.hasChild(txt_user)){
                    // password exist
                    final String getPassword = snapshot.child(txt_user).child("password").getValue(String.class);
                    final String getEmail = snapshot.child(txt_user).child("email").getValue(String.class);
                    //                final String getPassword = snapshot.child(txt_user).child("password").getValue().toString();
                    if(getPassword.equals(txt_password)){
                        Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this,selectionOfEle.class);
                        intent.putExtra("userName",txt_user);
                        intent.putExtra("Email",getEmail);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }
                }
                else{
                    Toast.makeText(Login.this, "User Does Not Exist" , Toast.LENGTH_SHORT).show();
                    user.setText("");
                    password.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

*/

    private void loginUser(String txt_user, String txt_password) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check username and password exist in database
                if(snapshot.hasChild(txt_user)){
                    // password exist
                    String getPassword = snapshot.child(txt_user).child("password").getValue(String.class).toString();
                    String getEmail = snapshot.child(txt_user).child("email").getValue(String.class).toString();
                    if(getPassword.equals(txt_password)){
//                        signIn(getEmail,getPassword,txt_user);
                    }
                    else{
                        Toast.makeText(Login.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }
                }
                else{
                    Toast.makeText(Login.this, "User Does Not Exist" , Toast.LENGTH_SHORT).show();
                    user.setText("");
                    password.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            nm.notify(NOTIFICATION_ID,notification);
//                            Toast.makeText(Login.this, "Login SuccessFull", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,selectionOfEle.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Login Faild", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account =  task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            String userId = auth.getCurrentUser().getUid().toString().trim();
                            
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference references = database.getReference("users");
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://meterreading-165ab-default-rtdb.firebaseio.com");
                            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    references.child(userId).child("email").setValue(auth.getCurrentUser().getEmail().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(Login.this,"Login SuccessFully", Toast.LENGTH_SHORT).show();
                                            nm.notify(NOTIFICATION_ID,notification);
                                            Intent intent = new Intent(Login.this,selectionOfEle.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Login.this, "Login Faild", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
//

}