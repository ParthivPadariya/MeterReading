package com.example.meterreading;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meterreading.ml.BestFp16;
import com.example.meterreading.ml.LiteModelKerasOcrFloat162;
import com.example.meterreading.ml.MobilenetV110224Quant;
import com.example.meterreading.ml.Model1;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class electicityBill extends AppCompatActivity {

    AutoCompleteTextView select_Bord;
    AutoCompleteTextView selectState;
    String[] eleBord = {"Paschim Gujarat Vij Company Limited(PGVCL)","Dakshin Gujarat Vij Company Limited","Gift Power Company Limited", "Madhya Gujarat Vij Company Limited", "Torrent Power Limited", "Uttar Gujarat Vij Company Limited"};

    ArrayAdapter<String> bordAdaperter ;
    ArrayAdapter<CharSequence> stateAdapter;

    int imageSize = 32;
    TextView ans;
    String bord;
    String state;
    String text;

    private String currentPhotoPath;
    ImageView back;
    Button electricity;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electicity_bill);


        selectState = findViewById(R.id.selectState);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(electicityBill.this,selectionOfEle.class));
            }
        });

        select_Bord = findViewById(R.id.selectBord);

        stateAdapter = ArrayAdapter.createFromResource(this,R.array.array_indian_state,R.layout.list_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectState.setAdapter(stateAdapter);
        selectState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                state = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(electicityBill.this, state, Toast.LENGTH_SHORT).show();
            }
        });

        bordAdaperter = new ArrayAdapter<String>(this,R.layout.list_item,eleBord);
        select_Bord.setAdapter(bordAdaperter);
        select_Bord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bord = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(electicityBill.this, bord, Toast.LENGTH_SHORT).show();
            }
        });

        ans = findViewById(R.id.predictans);
        electricity = findViewById(R.id.payEleBill);
        electricity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(bord) || TextUtils.isEmpty(state)){
                    Toast.makeText(electicityBill.this, "Fill the Details", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(electicityBill.this,billLayout.class);
                    if(ans.getText().toString().isEmpty()){
                        Toast.makeText(electicityBill.this, "Image Not Capture", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String currR = ans.getText().toString();
                        intent.putExtra("currentReading",currR);
                        intent.putExtra("activity","Electricity");
                        startActivity(intent);
                    }
                }
            }
        });

        Button btnCamera = findViewById(R.id.btnCamera);

//        Camera take picture
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                try {
                    File imageFile = File.createTempFile(fileName,".jpg",storageDirectory);
                    currentPhotoPath = imageFile.getAbsolutePath();

                    System.out.println(currentPhotoPath);
                    Uri imageUri = FileProvider.getUriForFile(electicityBill.this,"com.example.meterreading.fileprovider",imageFile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent,1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private void process_text(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();
        if(blocks.size() == 0){
            Toast.makeText(this, "No text Detect", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.Block block :firebaseVisionText.getBlocks()){
                text = block.getText();
//                ans.setText(text);
            }
            boolean result = text.matches("[0-9]+");

            if (result){
                ans.setText(text);
            }
            else{
                ans.setText("");
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
//            imgCamera.setBackground(null);
//            imgCamera.setImageBitmap(bitmap);
//            classifyimage(bitmap);

                if (bitmap==null){
                    Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show();
                }
                else{
                    FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
                    firebaseVisionTextDetector.detectInImage(firebaseVisionImage)
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                    process_text(firebaseVisionText);
                                }
                            });
                }
        }
    }
}