package com.theumajulian.flashcardapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flashcards.db";
    private static final int DATABASE_VERSION = 2;

    public static final class Categories implements BaseColumns {
        //static names for easier reference
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE = "image";
    }


    public static final class Flashcards implements BaseColumns {
        //static names for easier reference
        public static final String TABLE_NAME = "flashcards";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_FRONT_TEXT = "front_text";
        public static final String COLUMN_BACK_TEXT = "back_text";
        public static final String COLUMN_GRADE = "grade";
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, image BLOB)");
        db.execSQL("CREATE TABLE flashcards (id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER, front_text TEXT, back_text TEXT, grade TEXT, FOREIGN KEY(category_id) REFERENCES categories(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertCategory(String title, String description, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Categories.COLUMN_TITLE, title);
        values.put(Categories.COLUMN_DESCRIPTION, description);
        values.put(Categories.COLUMN_IMAGE, image);
        long id = db.insert(Categories.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Category> getAllCategories() {

        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String[] projection = {
                "id",
                Categories.COLUMN_TITLE,
                Categories.COLUMN_DESCRIPTION,
                Categories.COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                Categories.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        //cursor of all categories

        while (cursor.moveToNext()) {
            //add to categories list
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(Categories.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(Categories.COLUMN_DESCRIPTION));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(Categories.COLUMN_IMAGE));
            Category category = new Category(id, title, description, image);
            categories.add(category);
        }
        cursor.close();
        db.close();
        return categories;
    }

    public long insertFlashcard(long categoryId, String frontText, String backText, int grade) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        //set values to update
        values.put("category_id", categoryId);
        values.put("front_text", frontText);
        values.put("back_text", backText);
        values.put("grade", grade);

        long flashcardId = db.insert("flashcards", null, values);
        db.close();

        return flashcardId;
    }

    public List<FlashCard> getFlashcardsForCategory(int categoryId) {
        //returns a list of flashcards with matching category id
        SQLiteDatabase db = getReadableDatabase();
        List<FlashCard> flashcards = new ArrayList<>();

        Cursor cursor = db.query("flashcards", null, "category_id = ?", new String[] { String.valueOf(categoryId) }, null, null, null);
        //cursor of flashcards which have a matching category_id
        try {
            while (cursor.moveToNext()) {
                int frontTextIndex = cursor.getColumnIndex("front_text");
                int backTextIndex = cursor.getColumnIndex("back_text");
                int gradeIndex = cursor.getColumnIndex("grade");

                String frontText = cursor.getString(frontTextIndex);
                String backText = cursor.getString(backTextIndex);
                int grade = cursor.getInt(gradeIndex);

                FlashCard card = new FlashCard(categoryId, frontText, backText, grade);
                flashcards.add(card);
            }
        } catch (Exception e) {
            Log.e("Error", "Error while getting flashcards for category: " + e.getMessage());
        } finally {
            cursor.close();
            db.close();
        }

        return flashcards;
    }

    public void deleteCategory(long categoryId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Categories.TABLE_NAME, "id=?", new String[]{String.valueOf(categoryId)});
        db.close();
    }

    public byte[] getImageFromUri(Context context, Uri imageUri) {
        //returns an image in the form of a byte array
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);
            return imageBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[] {};
        }
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        //convert InputStream to byte[]
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public void editFlashcardGrade(int categoryId, String frontText, int newGrade) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Flashcards.COLUMN_GRADE, newGrade);//set value to update

        String selection = Flashcards.COLUMN_CATEGORY_ID + " = ? AND " + Flashcards.COLUMN_FRONT_TEXT + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId), frontText};//replace placeholder ?s

        db.update(Flashcards.TABLE_NAME, values, selection, selectionArgs);//perform update
        db.close();
    }

    public void deleteFlashcard(int categoryId, String frontText){
        SQLiteDatabase db = getWritableDatabase();
        String selection = Flashcards.COLUMN_CATEGORY_ID + " = ? AND " + Flashcards.COLUMN_FRONT_TEXT + " = ?";
        String[] selectionArgs = {String.valueOf(categoryId), frontText}; //replace placeholder ?s

        db.delete(Flashcards.TABLE_NAME, selection, selectionArgs);//perform delete
        db.close();
    }

}
