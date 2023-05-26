package com.theumajulian.flashcardapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;


public class QuizQuestionResultFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    int categoryId;

    public QuizQuestionResultFragment() {
        // Required empty public constructor
    }

    public static QuizQuestionResultFragment newInstance(String param1, String param2) {
        QuizQuestionResultFragment fragment = new QuizQuestionResultFragment();
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
        return inflater.inflate(R.layout.fragment_quiz_question_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        String feedback = bundle.getString("feedback");
        boolean isCorrect = bundle.getBoolean("isCorrect");
        categoryId = bundle.getInt("categoryId");

        //set views by Id
        TextView isCorrectTextView = getView().findViewById(R.id.text_view_quiz_result_correct);
        TextView feedbackTextView = getView().findViewById(R.id.text_view_quiz_result_feedback);
        Button nextButton = getView().findViewById(R.id.button_quiz_result_next);
        LottieAnimationView isCorrectAnim = getView().findViewById(R.id.lottie_is_correct);

        //set UI content
        if(isCorrect){
            isCorrectAnim.setAnimation(R.raw.correctanim); //set very good animation
            isCorrectTextView.setText("Correct!");
        }
        else{
            isCorrectAnim.setAnimation(R.raw.incorrectanim); //set cross animation
            isCorrectTextView.setText("Incorrect.");

        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToQuestionFragment();
            }
        });

        feedbackTextView.setText(feedback);

    }

    private void switchToQuestionFragment(){
        QuizQuestionFragment quizQuestionFragment = new QuizQuestionFragment();

        QuizActivity.currentIndex++;
        FlashCard nextFlashcard;
        if(QuizActivity.currentIndex < 5 && QuizActivity.quizFlashcards.size() > QuizActivity.currentIndex){
            nextFlashcard = QuizActivity.quizFlashcards.get(QuizActivity.currentIndex);
        }
        else{
            //Quiz over
            QuizActivity.currentIndex = 0;
            switchToFlashcardsActivity();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("frontText", nextFlashcard.getFrontText());
        bundle.putString("flashcardNumber", String.valueOf(QuizActivity.currentIndex - 1));
        bundle.putString("flashcardAnswer", nextFlashcard.getBackText());
        bundle.putInt("flashcardCategoryId", nextFlashcard.getCategoryId());

        quizQuestionFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout_quiz_container, quizQuestionFragment).commit();
    }

    private void switchToFlashcardsActivity(){
        Intent intent = new Intent(getActivity(), FlashcardsList.class);
        intent.putExtra("categoryId", categoryId);
        startActivity(intent);
    }
}