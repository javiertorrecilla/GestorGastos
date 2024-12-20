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
            // Abre o crea la base de datos
            db = context.openOrCreateDatabase("expenses_db", Context.MODE_PRIVATE, null);
            Log.d("DBHelper", "Base de datos abierta o creada exitosamente.");

            // Crea la tabla si no existe
            db.execSQL("CREATE TABLE IF NOT EXISTS expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, concept TEXT, date TEXT, amount REAL)");
            Log.d("DBHelper", "Tabla 'expenses' asegurada.");

            // Eliminamos la parte de inserción de datos predeterminados
            // Ahora la base de datos se inicializa vacía sin valores por defecto
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
                        expenses.add(new Expense(id, title, concept, date, amount));
                        Log.d("DBHelper", "Gasto recuperado: " + title);
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

    // Método para agregar un nuevo gasto
    public void addExpense(Expense expense) {
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("title", expense.getTitle());
            values.put("concept", expense.getConcept());
            values.put("date", expense.getDate());
            values.put("amount", expense.getAmount());
            db.insert("expenses", null, values);
            Log.d("DBHelper", "Gasto agregado: " + expense.getTitle());
        }
    }
}

