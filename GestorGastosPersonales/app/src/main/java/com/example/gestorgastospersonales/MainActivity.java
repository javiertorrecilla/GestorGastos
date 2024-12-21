package com.example.gestorgastospersonales;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addExpenseButton;
    private ExpenseAdapter adapter;
    private EditText searchEditText;
    private Spinner categorySpinner, sortSpinner;
    private List<Gasto> expenseList;
    private List<Gasto> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        sortSpinner = findViewById(R.id.sortSpinner);

        // Obtener los gastos de la base de datos
        GastoDbHelper dbHelper = new GastoDbHelper(this);
        dbHelper.borrarDatos();
        dbHelper.initGastos(this);
        expenseList = dbHelper.obtenerGastos();
        filteredList = new ArrayList<>(expenseList);

        // Adaptador para la lista de gastos
        adapter = new ExpenseAdapter(this, filteredList);
        listView.setAdapter(adapter);

        // Filtro de búsqueda
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                updateFilteredList();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Configurar el Spinner de categorías
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                updateFilteredList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        // Configurar el Spinner de ordenación
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                updateFilteredList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    private void updateFilteredList() {
        String searchQuery = searchEditText.getText().toString().toLowerCase().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedSortOption = sortSpinner.getSelectedItem().toString();

        filteredList.clear();

        // Filtrar por búsqueda y categoría
        for (Gasto expense : expenseList) {
            boolean matchesSearch = expense.getDescripcion().toLowerCase().contains(searchQuery) ||
                    expense.getLugar().toLowerCase().contains(searchQuery);

            boolean matchesCategory = selectedCategory.equals("Todas") ||
                    expense.getCategoria().equalsIgnoreCase(selectedCategory);

            if (matchesSearch && matchesCategory) {
                filteredList.add(expense);
            }
        }

        // Ordenar según la opción seleccionada
        switch (selectedSortOption) {
            case "Más recientes":
                Collections.sort(filteredList, (o1, o2) -> {
                    return o2.getFecha().compareTo(o1.getFecha()); // Ordenar por fecha (descendente)
                });
                break;

            case "Más antiguos":
                Collections.sort(filteredList, Comparator.comparing(Gasto::getFecha));
                break;

            case "Más caros":
                Collections.sort(filteredList, (o1, o2) -> {
                    return Double.compare(o2.getCantidad(), o1.getCantidad()); // Ordenar por cantidad (descendente)
                });
                break;

            case "Más baratos":
                Collections.sort(filteredList, Comparator.comparingDouble(Gasto::getCantidad));
                break;
        }

        // Notificar al adaptador para que se actualice la lista
        adapter.notifyDataSetChanged();
    }
}
