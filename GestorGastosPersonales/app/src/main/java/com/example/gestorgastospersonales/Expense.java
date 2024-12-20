package com.example.gestorgastospersonales;

public class Expense {

    private int id; // ID del gasto
    private String title; // Título del gasto
    private String concept; // Concepto del gasto
    private String date; // Fecha del gasto
    private double amount; // Cantidad del gasto

    // Constructor para crear un nuevo gasto (sin id)
    public Expense(String title, String concept, String date, double amount) {
        this.title = title;
        this.concept = concept;
        this.date = date;
        this.amount = amount;
    }

    // Constructor cuando se obtiene un gasto de la base de datos (con id)
    public Expense(int id, String title, String concept, String date, double amount) {
        this.id = id;
        this.title = title;
        this.concept = concept;
        this.date = date;
        this.amount = amount;
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
}
