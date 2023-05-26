package com.theumajulian.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class FlashcardContentActivity extends AppCompatActivity {

    private TextView frontText;
    private TextView backText;
    int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_content);

        frontText = findViewById(R.id.text_view_flashcard_details_front_text);
        backText = findViewById(R.id.text_view_flashcard_details_back_text);

        Intent intent = getIntent();
        String cardFrontText = intent.getStringExtra("frontText");
        String cardBackText = intent.getStringExtra("backText");

        categoryId = intent.getIntExtra("categoryId", 1);
        frontText.setText(cardFrontText);
        backText.setText(cardBackText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //on pressing back
        int id = item.getItemId();

        if(id == android.R.id.home){
            //handle adding intent extras to load other page well
            Intent intent = new Intent(this, FlashcardsList.class);
            intent.putExtra("categoryId", categoryId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}