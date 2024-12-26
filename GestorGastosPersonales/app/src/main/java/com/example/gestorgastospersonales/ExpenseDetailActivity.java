package com.example.gestorgastospersonales;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView fechaTextView, lugarTextView, descripcionTextView, categoriaTextView, cantidadTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // Inicializar vistas
        fechaTextView = findViewById(R.id.fechaTextView);
        lugarTextView = findViewById(R.id.lugarTextView);
        descripcionTextView = findViewById(R.id.descripcionTextView);
        categoriaTextView = findViewById(R.id.categoriaTextView);
        cantidadTextView = findViewById(R.id.cantidadTextView);

        // Obtener ID del gasto desde el Intent
        int expenseId = getIntent().getIntExtra("id", -1);
        if (expenseId != -1) {
            // Obtener los detalles del gasto desde la base de datos
            GastoDbHelper dbHelper = new GastoDbHelper(this);
            Gasto gasto = dbHelper.obtenerGastoPorId(expenseId);

            // Mostrar los detalles en las vistas
            if (gasto != null) {
                fechaTextView.setText(gasto.getFecha());
                lugarTextView.setText(gasto.getLugar());
                descripcionTextView.setText(gasto.getDescripcion());
                categoriaTextView.setText(gasto.getCategoria());
                cantidadTextView.setText(String.valueOf(gasto.getCantidad()));
            }
        }

        // Configurar botón para cerrar la actividad y regresar
        Button cerrarButton = findViewById(R.id.cerrarButton);
        cerrarButton.setOnClickListener(v -> finish()); // Cierra la actividad
    }
}
