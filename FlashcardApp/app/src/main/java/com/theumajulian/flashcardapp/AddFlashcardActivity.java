package com.theumajulian.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddFlashcardActivity extends AppCompatActivity {

    private EditText editTextFrontText;
    private EditText editTextBackText;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        setViews();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(AddFlashcardActivity.this);
                String frontText = editTextFrontText.getText().toString();
                String backText = editTextBackText.getText().toString();
                Intent intent = getIntent();
                int categoryId = intent.getIntExtra("categoryId", -1);
                long flashcardId = dbHelper.insertFlashcard(categoryId, frontText, backText, 5);
                Intent navigateToFlashcardsIntent = new Intent(AddFlashcardActivity.this, FlashcardsList.class);
                navigateToFlashcardsIntent.putExtra("categoryId", categoryId);
                startActivity(navigateToFlashcardsIntent);
                //get text from text views
                //save to db
            }
        });
    }

    private void setViews() {
        buttonSave = findViewById(R.id.button_add_flashcard_submit);
        editTextBackText = findViewById(R.id.edit_text_add_flashcard_back_text);
        editTextFrontText = findViewById(R.id.edit_text_add_flashcard_front_text);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FlashcardsList.class);
        intent.putExtra("categoryId", 2);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent(this, FlashcardsList.class);
            int categoryId = getIntent().getIntExtra("categoryId", 2);
            intent.putExtra("categoryId", categoryId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}