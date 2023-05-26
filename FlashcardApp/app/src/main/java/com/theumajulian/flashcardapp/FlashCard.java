package com.theumajulian.flashcardapp;

public class FlashCard {
    private int categoryId;
    private String frontText;
    private String backText;
    private int grade;

    public FlashCard(int categoryId, String frontText, String backText, int grade) {
        this.categoryId = categoryId;
        this.frontText = frontText;
        this.backText = backText;
        this.grade = grade;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    public int getGrade() {
        return grade;
    }
}

