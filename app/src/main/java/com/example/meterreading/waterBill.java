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
import com.example.meterreading.ml.Model1;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.yalantis.ucrop.UCrop;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;

public class waterBill extends AppCompatActivity {

    Button water;
    AutoCompleteTextView select_Bord;
    ArrayAdapter<String> bordAdaperter ;
    ArrayAdapter<CharSequence> stateAdapter;
    ImageView back;
    TextView ans;

    int imageSize = 32;
    String bord;
    String text;
    private String currentPhotoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_bill);

        select_Bord = findViewById(R.id.selectWaterBord);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(waterBill.this,selectionOfEle.class));
            }
        });

        stateAdapter = ArrayAdapter.createFromResource(this,R.array.waterBoard,R.layout.list_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_Bord.setAdapter(stateAdapter);
        select_Bord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bord = adapterView.getItemAtPosition(i).toString();
//                select_Bord.setText(item);
//                Toast.makeText(waterBill.this, bord, Toast.LENGTH_SHORT).show();
            }
        });

        ans = findViewById(R.id.predictans);

        water = findViewById(R.id.water);
        water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(bord)){
                    Toast.makeText(waterBill.this, "Fill the Details", Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(waterBill.this, ans.getText().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(waterBill.this,billLayout.class);
                    String currR = ans.getText().toString();

                    if(TextUtils.isEmpty(currR)){
                        Toast.makeText(waterBill.this, "Image Not Capture", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        intent.putExtra("currentReading",currR);
                        intent.putExtra("activity","Water");
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

                    String desi_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

                    System.out.println(currentPhotoPath);
                    Uri imageUri = FileProvider.getUriForFile(waterBill.this,"com.example.meterreading.fileprovider",imageFile);

                    UCrop.Options options = new UCrop.Options();

                    UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(),desi_uri)))
                            .withOptions(options)
                            .withAspectRatio(0,0)
                            .useSourceImageAspectRatio()
                            .withMaxResultSize(2000,2000)
                            .start(waterBill.this);

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