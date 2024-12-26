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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addExpenseButton;
    private ExpenseAdapter adapter;
    private EditText searchEditText;
    private Spinner categorySpinner, sortSpinner, timeSpinner;
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
        timeSpinner = findViewById(R.id.timeSpinner); // Nuevo Spinner

        GastoDbHelper dbHelper = new GastoDbHelper(this);
        expenseList = dbHelper.obtenerGastos();
        filteredList = new ArrayList<>(expenseList);

        adapter = new ExpenseAdapter(this, filteredList);
        listView.setAdapter(adapter);

        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Configurar búsqueda por texto
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

        // Configurar Spinner de categorías
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

        // Configurar Spinner de ordenación
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

        // Configurar Spinner de tiempo
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_options, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        String selectedTimeOption = timeSpinner.getSelectedItem().toString();

        filteredList.clear();

        // Obtener fechas actuales y límite
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DATE, -6);
        Date lastWeek = calendar.getTime();
        calendar.add(Calendar.DATE, -23); // Ya restamos 7, sumamos 23 más
        Date lastMonth = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Gasto expense : expenseList) {
            boolean matchesSearch = expense.getDescripcion().toLowerCase().contains(searchQuery) ||
                    expense.getLugar().toLowerCase().contains(searchQuery);

            boolean matchesCategory = selectedCategory.equals("Todas") ||
                    expense.getCategoria().equalsIgnoreCase(selectedCategory);

            boolean matchesTime = true; // Default is true (if "Todos" is selected)
            try {
                Date expenseDate = dateFormat.parse(expense.getFecha());
                switch (selectedTimeOption) {
                    case "Hoy":
                        matchesTime = dateFormat.format(expenseDate).equals(dateFormat.format(today));
                        break;
                    case "Ayer":
                        matchesTime = dateFormat.format(expenseDate).equals(dateFormat.format(yesterday));
                        break;
                    case "Última Semana":
                        matchesTime = expenseDate.after(lastWeek) && !expenseDate.after(today);
                        break;
                    case "Último Mes":
                        matchesTime = expenseDate.after(lastMonth) && !expenseDate.after(today);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (matchesSearch && matchesCategory && matchesTime) {
                filteredList.add(expense);
            }
        }

        switch (selectedSortOption) {
            case "Más recientes":
                Collections.sort(filteredList, (o1, o2) -> o2.getFecha().compareTo(o1.getFecha()));
                break;

            case "Más antiguos":
                Collections.sort(filteredList, Comparator.comparing(Gasto::getFecha));
                break;

            case "Más caros":
                Collections.sort(filteredList, (o1, o2) -> Double.compare(o2.getCantidad(), o1.getCantidad()));
                break;

            case "Más baratos":
                Collections.sort(filteredList, Comparator.comparingDouble(Gasto::getCantidad));
                break;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Actualizar lista después de añadir un gasto
            GastoDbHelper dbHelper = new GastoDbHelper(this);
            expenseList = dbHelper.obtenerGastos();
            filteredList.clear();
            filteredList.addAll(expenseList);
            adapter.notifyDataSetChanged();
        }
    }
}
