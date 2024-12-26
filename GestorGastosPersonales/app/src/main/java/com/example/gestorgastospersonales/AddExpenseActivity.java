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
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText fechaEditText, lugarEditText, descripcionEditText, cantidadEditText;
    private Spinner categoriaSpinner;
    private Button guardarButton, cancelarButton;

    private TextView fechaSeleccionadaTextView;
    private Button abrirCalendarioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Inicializar vistas
        lugarEditText = findViewById(R.id.lugarEditText);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        cantidadEditText = findViewById(R.id.cantidadEditText);
        categoriaSpinner = findViewById(R.id.categoriaSpinner);
        guardarButton = findViewById(R.id.guardarButton);
        cancelarButton = findViewById(R.id.cancelarButton);
        fechaSeleccionadaTextView = findViewById(R.id.fechaSeleccionadaTextView);
        abrirCalendarioButton = findViewById(R.id.abrirCalendarioButton);

        // Configurar spinner de categorías
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoriaSpinner.setAdapter(adapter);

        abrirCalendarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        // Configurar botón Guardar
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarGasto();
            }
        });

        // Configurar botón Cancelar
        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cerrar actividad
            }
        });
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

    private void agregarGasto() {
        // Obtener datos del formulario
        String fecha = fechaSeleccionadaTextView.getText().toString().trim();
        String lugar = lugarEditText.getText().toString().trim();
        String descripcion = descripcionEditText.getText().toString().trim();
        String categoria = categoriaSpinner.getSelectedItem().toString();
        String cantidadStr = cantidadEditText.getText().toString().trim();

        // Validar entrada
        if (fecha.isEmpty() || lugar.isEmpty() || descripcion.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar en la base de datos
        GastoDbHelper dbHelper = new GastoDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GastoContract.GastoEntry.COLUMN_NAME_FECHA, fecha);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_LUGAR, lugar);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION, descripcion);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA, categoria);
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD, cantidad);

        long newRowId = db.insert(GastoContract.GastoEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Gasto añadido correctamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Indicar éxito
            finish(); // Cerrar actividad
        } else {
            Toast.makeText(this, "Error al añadir gasto", Toast.LENGTH_SHORT).show();
        }
    }
}