package com.theumajulian.flashcardapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FlashcardsList extends AppCompatActivity {

    DatabaseHelper dbHelper;
    RecyclerView recyclerView;
    Button addFlashcardButton;
    Button startQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards_list);
        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("categoryId", 1);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view_flashcards);
        addFlashcardButton = findViewById(R.id.button_add_flashcard);
        startQuizButton = findViewById(R.id.button_start_quiz);

        List<FlashCard> flashcards = dbHelper.getFlashcardsForCategory(categoryId);


        FlashCard[] categoryArray = new FlashCard[flashcards.size()];
        flashcards.toArray(categoryArray);

        FlashcardsAdapter adapter = new FlashcardsAdapter(categoryArray);
        recyclerView.setAdapter(adapter);

        addFlashcardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //navigate to add flashcard form
                Intent navigateToAddFlashcardActivity = new Intent(FlashcardsList.this, AddFlashcardActivity.class);
                navigateToAddFlashcardActivity.putExtra("categoryId", categoryId);
                startActivity(navigateToAddFlashcardActivity);
            }
        });

        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navigateToQuizActivity = new Intent(FlashcardsList.this, QuizActivity.class);
                navigateToQuizActivity.putExtra("categoryId", categoryId);
                startActivity(navigateToQuizActivity);
            }
        });

        if(!(flashcards.size() > 0)){
            //if there are no flashcards, display text showing user to add one by clicking below
            findViewById(R.id.text_view_empty_flashcards).setVisibility(View.VISIBLE);
        }
    }
}