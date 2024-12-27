package com.example.gestorgastospersonales;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText lugarEditText, descripcionEditText, cantidadEditText;
    private Spinner categoriaSpinner;
    private int gastoId; // ID del gasto a editar

    private TextView fechaSeleccionadaTextView;
    private Button abrirCalendarioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        // Inicializar vistas
        fechaSeleccionadaTextView = findViewById(R.id.fechaSeleccionadaTextView);
        abrirCalendarioButton = findViewById(R.id.abrirCalendarioButton);
        lugarEditText = findViewById(R.id.lugarEditText);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        cantidadEditText = findViewById(R.id.cantidadEditText);
        categoriaSpinner = findViewById(R.id.categoriaSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

        // Obtener datos del Intent
        Intent intent = getIntent();
        gastoId = intent.getIntExtra("gastoId", -1); // ID del gasto a editar
        String fecha = intent.getStringExtra("fecha");
        String lugar = intent.getStringExtra("lugar");
        String descripcion = intent.getStringExtra("descripcion");
        String categoria = intent.getStringExtra("categoria");
        double cantidad = intent.getDoubleExtra("cantidad", 0.0);

        int posicion = -1;
        for (int i = 0; i < categoriaSpinner.getCount(); i++) {
            if (categoriaSpinner.getItemAtPosition(i).toString().equals(categoria)) {
                posicion = i;
                break;
            }
        }

        if (posicion >= 0) {
            categoriaSpinner.setSelection(posicion);
        } else {
            Toast.makeText(this, "Categoría no encontrada.", Toast.LENGTH_SHORT).show();
        }

        // Configurar los campos con los datos actuales del gasto
        fechaSeleccionadaTextView.setText(fecha);
        lugarEditText.setText(lugar);
        descripcionEditText.setText(descripcion);
        cantidadEditText.setText(String.valueOf(cantidad));

        // Seleccionar categoría (puedes agregar lógica para establecer la categoría actual)
        abrirCalendarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        // Configurar botones
        Button guardarButton = findViewById(R.id.guardarButton);
        guardarButton.setOnClickListener(v -> guardarCambios());

        Button cancelarButton = findViewById(R.id.cancelarButton);
        cancelarButton.setOnClickListener(v -> finish());
    }

    private void mostrarCalendario() {
        // Obtiene la fecha actual
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Muestra el selector de fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Actualiza el TextView con la fecha seleccionada
            String fechaSeleccionada = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            fechaSeleccionadaTextView.setText(fechaSeleccionada);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void guardarCambios() {
        // Obtener los datos modificados
        String fecha = fechaSeleccionadaTextView.getText().toString().trim();
        String lugar = lugarEditText.getText().toString().trim();
        String descripcion = descripcionEditText.getText().toString().trim();
        String categoria = categoriaSpinner.getSelectedItem().toString();
        String cantidadStr = cantidadEditText.getText().toString().trim();

        if (fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar en la base de datos
        GastoDbHelper dbHelper = new GastoDbHelper(this);

        ContentValues values = new ContentValues();
        values.put(GastoContract.GastoEntry.COLUMN_NAME_FECHA, fecha);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_LUGAR, lugar);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION, descripcion);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA, categoria);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD, cantidad);

        int rowsAffected = dbHelper.getWritableDatabase().update(
                GastoContract.GastoEntry.TABLE_NAME,
                values,
                GastoContract.GastoEntry._ID + "=?",
                new String[]{String.valueOf(gastoId)}
        );

        if (rowsAffected > 0) {
            Toast.makeText(this, "Gasto actualizado correctamente.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Cierra la actividad
        } else {
            Toast.makeText(this, "Error al actualizar el gasto.", Toast.LENGTH_SHORT).show();
        }
    }
}