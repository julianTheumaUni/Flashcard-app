package com.theumajulian.flashcardapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Category[] categories;

    public CategoriesAdapter(Category[] categories){
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card_layout, parent, false);
        return new CategoryViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories[position]);
    }

    @Override
    public int getItemCount() {
         return categories.length;
    }

    static class  CategoryViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView title;
        Context context;
        Category _category;
        TextView deleteButton;
        boolean isInDeleteMode;
        DatabaseHelper dbHelper;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relative_layout_category_card_background);
            title = itemView.findViewById(R.id.text_view_category_card_title);
            deleteButton = itemView.findViewById(R.id.button_category_card_delete);
            context = itemView.getContext();
            dbHelper = new DatabaseHelper(itemView.getContext());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isInDeleteMode){
                        deleteButton.setVisibility(View.INVISIBLE);
                        isInDeleteMode = false;
                        return;
                    }
                    Intent navigateToFlashcardListIntent = new Intent(view.getContext(), FlashcardsList.class);
                    navigateToFlashcardListIntent.putExtra("categoryId", _category.getId());
                    view.getContext().startActivity(navigateToFlashcardListIntent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteButton.setVisibility(View.VISIBLE);
                    isInDeleteMode = true;
                    return true;
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Are you sure you want to delete " + _category.getTitle() + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.deleteCategory(_category.getId());
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            view.getContext().startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            });
        }

        public void bind(Category category){
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(category.getImage(), 0, category.getImage().length);
                Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                relativeLayout.setBackground(drawable);

            }
            catch (Exception e){
                Drawable drawable = context.getResources().getDrawable(R.color.dark_blue_secondary, context.getTheme());
                relativeLayout.setBackground(drawable);
            }
                _category = category;

                title.setText(category.getTitle());

        }
    }
}
