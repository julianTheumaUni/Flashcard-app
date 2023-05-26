package com.theumajulian.flashcardapp;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManagerNonConfig;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;


public class QuizActivity extends AppCompatActivity {

    private TextView frontText;
    private EditText editTextAnswer;
    private Button submitButton;
    private List<FlashCard> flashCardList;
    public static int currentIndex;
    private int categoryId;
    private FlashCard currentFlashcard;
    public static List<FlashCard> quizFlashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        currentIndex = 0;

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        categoryId = getIntent().getIntExtra("categoryId", 1);
        flashCardList = dbHelper.getFlashcardsForCategory(categoryId);

        Collections.shuffle(flashCardList); //randomize selection
        if(flashCardList.size() > 5){
            quizFlashcards = flashCardList.subList(0, 5); //typical 5 value question set
        }
        else{
            quizFlashcards = flashCardList.subList(0, flashCardList.size() - 1); //Quiz on all flashcards if less than 5 are present
        }
        FlashCard firstFlashCard = flashCardList.get(0);
        QuizQuestionFragment quizQuestionFragment = new QuizQuestionFragment();

        Bundle bundle = new Bundle();
        //prepare data for first question fragment
        bundle.putString("frontText", firstFlashCard.getFrontText());
        bundle.putString("flashcardNumber", String.valueOf(currentIndex + 1));
        bundle.putString("flashcardAnswer", firstFlashCard.getBackText());
        bundle.putInt("flashcardCategoryId", firstFlashCard.getCategoryId());

        quizQuestionFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_quiz_container, quizQuestionFragment);
        fragmentTransaction.commit(); //display fragment
    }

}