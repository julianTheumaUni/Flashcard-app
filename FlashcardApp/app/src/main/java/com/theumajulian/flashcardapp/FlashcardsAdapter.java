package com.theumajulian.flashcardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FlashcardsAdapter extends RecyclerView.Adapter<FlashcardsAdapter.FlashcardViewHolder> {

    private FlashCard[] flashCards;

    public FlashcardsAdapter(FlashCard[] flashCards) {
        this.flashCards = flashCards;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_flashcard_list, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        holder.bind(flashCards[position]);
    }

    @Override
    public int getItemCount() {
        return flashCards.length;
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        Context context;
        FlashCard _flashCard;
        TextView frontText;
        TextView gradeText;
        TextView deleteButton;
        boolean isInDeleteMode = false;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);

            context = itemView.getContext();
            frontText = itemView.findViewById(R.id.text_view_flashcard_front_text);
            gradeText = itemView.findViewById(R.id.text_view_flashcard_grade);
            deleteButton = itemView.findViewById(R.id.text_view_delete_flashcard);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isInDeleteMode){
                        //clicking outside of delete button while in delete mode(delete button visible) will remove the delete button from being visible
                        deleteButton.setVisibility(View.GONE);
                        isInDeleteMode = false;
                        return;
                    }
                    String cardFrontText = _flashCard.getFrontText();
                    String cardBackText = _flashCard.getBackText();

                    Intent navigateToFlashcardDetailsIntent = new Intent(itemView.getContext(), FlashcardContentActivity.class);
                    navigateToFlashcardDetailsIntent.putExtra("frontText", cardFrontText);
                    navigateToFlashcardDetailsIntent.putExtra("backText", cardBackText);
                    navigateToFlashcardDetailsIntent.putExtra("categoryId", _flashCard.getCategoryId());
                    view.getContext().startActivity(navigateToFlashcardDetailsIntent);

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //display delete button
                    deleteButton.setVisibility(View.VISIBLE);
                    isInDeleteMode = true;
                    return true;
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //perform delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Are you sure you want to delete " + _flashCard.getFrontText() + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //on clicking yes
                            DatabaseHelper dbHelper = new DatabaseHelper(view.getContext());
                            dbHelper.deleteFlashcard(_flashCard.getCategoryId(), _flashCard.getFrontText());
                            Intent intent = new Intent(view.getContext(), FlashcardsList.class);
                            intent.putExtra("categoryId", _flashCard.getCategoryId());
                            view.getContext().startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });
        }

        public void bind(FlashCard flashCard){
            _flashCard = flashCard;

            int grade = flashCard.getGrade();
            String gradeString;

            switch (grade) {
                case 1:
                    gradeString = "A";
                    break;
                case 2:
                    gradeString = "B";
                    break;
                case 3:
                    gradeString = "C";
                    break;
                case 4:
                    gradeString = "D";
                    break;
                case 5:
                    gradeString = "F";
                    break;
                default:
                    gradeString = "N/A";
                    break;
            }

            gradeText.setText(gradeString);
            frontText.setText(flashCard.getFrontText());
        }
    }
}
