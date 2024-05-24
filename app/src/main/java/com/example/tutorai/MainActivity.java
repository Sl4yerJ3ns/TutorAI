package com.example.tutorai;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log; // Import for logging
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Import for Toast notifications

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.InvalidAPIKeyException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView = (TextView)findViewById(R.id.textView);
    }

    public void buttonCallGeminiApi(View view){
        EditText result = (EditText) findViewById(R.id.Result);
        String editTextValue = result.getText().toString();

        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro-vision",
                /* apiKey */ "AIzaSyBx6F6-qxtaTgFJ5g_uE-UzocLo3KIgpjI"); // Replace with your actual API key

        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // Check if image resource is found before adding
        File imgFile = new File(Environment.getExternalStorageDirectory(), "pixel_background.png");
        Bitmap image1 = BitmapFactory.decodeFile(image1.getAbsolutePath());

        ListenableFuture<GenerateContentResponse> response;
        if (image1 != null) {
            Content content = new Content.Builder()
                    .addText(editTextValue)
                    .addImage(image1)
                    .build();
            response = model.generateContent(content);
        } else {
            // Use text-only generation
            Content textContent = new Content.Builder().addText(editTextValue).build();
            response = model.generateContent(textContent);
            Toast.makeText(this, "Image not found! Using text-only input.", Toast.LENGTH_SHORT).show();
        }

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                textView.setText(resultText);
                System.out.println(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof InvalidAPIKeyException) {
                    Log.e("MainActivity", "Invalid API Key!", t);
                    Toast.makeText(MainActivity.this, "Invalid API Key! Please check your key.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Error generating content:", t);
                    Toast.makeText(MainActivity.this, "Error generating content. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        }, this.getMainExecutor());
    }
}
