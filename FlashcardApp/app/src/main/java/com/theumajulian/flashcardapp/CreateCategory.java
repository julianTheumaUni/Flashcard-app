package com.theumajulian.flashcardapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateCategory extends AppCompatActivity {
    private EditText titleEditText;
    private ImageView selectImageButton;
    private Button submitButton;
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private byte[] selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        dbHelper = new DatabaseHelper(this);
        setReferences();
        setImagePickerLauncher();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the image picker
                imagePickerLauncher.launch("image/*");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String description = "";
                dbHelper.insertCategory(title, description, selectedImage);
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void setReferences() {
        titleEditText = findViewById(R.id.edit_text_add_category_title);
        selectImageButton = findViewById(R.id.button_add_category_image_button);
        submitButton = findViewById(R.id.button_add_category_submit);
    }

    private void setImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            selectedImage = dbHelper.getImageFromUri(CreateCategory.this, result);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(selectedImage, 0, selectedImage.length);
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                            selectImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            selectImageButton.setImageDrawable(drawable);
                        }
                    }
                });
    }
}