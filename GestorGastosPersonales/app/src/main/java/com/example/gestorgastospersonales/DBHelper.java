package com.example.gestorgastospersonales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static SQLiteDatabase db;

    public DBHelper(Context context) {
        try {
            db = context.openOrCreateDatabase("expenses_db", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, concept TEXT, date TEXT, amount REAL, category TEXT)");
        } catch (Exception e) {
            Log.e("DBHelper", "Error al crear o abrir la base de datos: ", e);
        }
    }

    // Método para obtener todos los gastos
    public static List<Expense> getAllExpenses(Context context) {
        List<Expense> expenses = new ArrayList<>();
        if (db != null) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT * FROM expenses", null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        String concept = cursor.getString(cursor.getColumnIndex("concept"));
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                        String category = cursor.getString(cursor.getColumnIndex("category"));
                        expenses.add(new Expense(id, title, concept, date, amount, category));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e("DBHelper", "Error al obtener los gastos: ", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return expenses;
    }

    public void addExpense(Expense expense) {
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("title", expense.getTitle());
            values.put("concept", expense.getConcept());
            values.put("date", expense.getDate());
            values.put("amount", expense.getAmount());
            values.put("category", expense.getCategory());
            db.insert("expenses", null, values);
        }
    }
}
