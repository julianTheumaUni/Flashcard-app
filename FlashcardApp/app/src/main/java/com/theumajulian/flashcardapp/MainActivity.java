package com.theumajulian.flashcardapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button selectImageButton;
    private Button addCategoryButton;
    private DatabaseHelper dbHelper;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private int categoryId;
    private byte[] selectedImage;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find required views by ID
        addCategoryButton = findViewById(R.id.button_main_add_categories);
        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view_categories);

        setOnClickListeners();
        getAllCategories();

    }

    private void getAllCategories() {
        List<Category> categories = dbHelper.getAllCategories();
        Category[] categoryArray = new Category[categories.size()];
        categories.toArray(categoryArray);

        if(!(categoryArray.length > 0)){
            findViewById(R.id.text_view_empty_categories).setVisibility(View.VISIBLE);
        }

        CategoriesAdapter adapter = new CategoriesAdapter(categoryArray);
        recyclerView.setAdapter(adapter); //fill recycler view with these categories
    }

    private void setOnClickListeners() {
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to form
                Intent navigateToAddCategoryIntent = new Intent(MainActivity.this, CreateCategory.class);
                startActivity(navigateToAddCategoryIntent);
            }
        });

    }
}
