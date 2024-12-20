package com.example.gestorgastospersonales;

public class Expense {

    private int id;
    private String title;
    private String concept;
    private String date;
    private double amount;
    private String category;

    public Expense(String title, String concept, String date, double amount, String category) {
        this.title = title;
        this.concept = concept;
        this.date = date;
        this.amount = amount;
        this.category = category;
    }

    public Expense(int id, String title, String concept, String date, double amount, String category) {
        this.id = id;
        this.title = title;
        this.concept = concept;
        this.date = date;
        this.amount = amount;
        this.category = category;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getConcept() {
        return concept;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static final String CATEGORY_FOOD = "Comida";
    public static final String CATEGORY_TRANSPORT = "Transporte";
    public static final String CATEGORY_UTILITY = "Servicios";
    public static final String CATEGORY_ENTERTAINMENT = "Entretenimiento";
    public static final String CATEGORY_OTHER = "Otros";

    public static String[] getCategories() {
        return new String[] {CATEGORY_FOOD, CATEGORY_TRANSPORT, CATEGORY_UTILITY, CATEGORY_ENTERTAINMENT, CATEGORY_OTHER};
    }
}
