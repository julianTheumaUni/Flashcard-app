package com.theumajulian.flashcardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class QuizQuestionFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private boolean hasVisitedSettings;
    EditText answerEditText;
    Button button;
    int categoryId;

    public QuizQuestionFragment() {
        // Required empty public constructor
    }

    public static QuizQuestionFragment newInstance(String param1, String param2) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        String frontText = bundle.getString("frontText");
        String flashcardSavedAnswer = bundle.getString("flashcardAnswer");
        categoryId = bundle.getInt("flashcardCategoryId");

        TextView textViewFrontText = getView().findViewById(R.id.text_view_quiz_question_flashcard_front_text);
        textViewFrontText.setText(frontText);


        button = getView().findViewById(R.id.button_submit_quiz_answer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on press submit
                answerEditText = getView().findViewById(R.id.edit_text_quiz_question_answer);
                String answer = answerEditText.getText().toString();

                answerEditText.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                LottieAnimationView animationView = getView().findViewById(R.id.lottie_spinner_quiz_question);
                animationView.setVisibility(View.VISIBLE);

                checkAnswer(answer, frontText, flashcardSavedAnswer, categoryId);
            }
        });

    }

    private void checkAnswer(String userAnswer, String frontText, String savedAnswer, int flashcardCategoryId){
        int timeout = 3600;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://flashcards-api-qfvq.vercel.app/api?frontCard=" + frontText + "&savedAnswer=" + savedAnswer + "&userAnswer=" +  userAnswer, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String grade;
                String explanation;

                try{
                    grade = response.getString("grade");
                    explanation = response.getString("feedback");

                    int intGrade;
                    if(grade.equals(new String("A"))) intGrade = 1;
                    else if(grade.equals(new String("B"))) intGrade = 2;
                    else if(grade.equals(new String("C"))) intGrade = 3;
                    else if(grade.equals(new String("D"))) intGrade = 4;
                    else if(grade.equals(new String("F"))) intGrade = 5;
                    else intGrade = 0;

                    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                    dbHelper.editFlashcardGrade(flashcardCategoryId, frontText,intGrade); //update grade
                    switchToAnswerFragment(explanation, intGrade);
                }
                catch (Exception e){
                    Log.e("Error", "Error correcting your answer");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error.", error.toString());

                if(error instanceof NoConnectionError){
                    //if error occured because of no internet connection...
                    //alert to go to settings to connect to wifi or switch on mobile data
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("No internet connection");
                    builder.setMessage("The quiz requires an internet connection. How do you wish to connect?");
                    builder.setPositiveButton("Wi-Fi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            hasVisitedSettings = true;
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Mobile Data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            hasVisitedSettings = true;

                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(
                timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);

    }



    public void switchToAnswerFragment(String feedback, int grade)
    {
        QuizQuestionResultFragment quizQuestionResultFragment = new QuizQuestionResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("feedback", feedback);

        if(grade <= 3 && grade != 0){
            bundle.putBoolean("isCorrect", true);
        }
        else{
            bundle.putBoolean("isCorrect", false);
        }

        bundle.putInt("categoryId", categoryId);

        quizQuestionResultFragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout_quiz_container, quizQuestionResultFragment).commit();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(hasVisitedSettings){
            if(!isConnectedToInternet()){
                //alert user to connect to internet again
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("No internet connection");
                builder.setMessage("The quiz requires an internet connection. How do you wish to connect?");
                builder.setPositiveButton("Wi-Fi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        hasVisitedSettings = true;
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Mobile Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_DATA_ROAMING_SETTINGS);
                        hasVisitedSettings = true;

                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                answerEditText.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                LottieAnimationView animationView = getView().findViewById(R.id.lottie_spinner_quiz_question);
                animationView.setVisibility(View.GONE);
            }
        }

    }

    private boolean isConnectedToInternet(){
        //return false if not connected to internet. true if connected.
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}