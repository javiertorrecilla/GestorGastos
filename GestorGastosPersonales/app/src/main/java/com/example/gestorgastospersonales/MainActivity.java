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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int EDIT_EXPENSE_REQUEST_CODE = 2;
    private ListView listView;
    private Button addExpenseButton;
    private ExpenseAdapter adapter;
    private EditText searchEditText;
    private Spinner categorySpinner, sortSpinner, timeSpinner;
    private List<Gasto> expenseList;
    private List<Gasto> filteredList;
    private Map<String, String> categoryMap = new HashMap<>();


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

        // Configurar clic en ítems de la lista para abrir detalles
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gasto selectedExpense = filteredList.get(position);
                abrirDetalleGasto(selectedExpense.getId());  // Llama al método para abrir los detalles
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

        categoryMap.put("Comida", getString(R.string.category_food));
        categoryMap.put("Transporte", getString(R.string.category_transport));
        categoryMap.put("Ocio", getString(R.string.category_fun));
        categoryMap.put("Vivienda", getString(R.string.category_home));
        categoryMap.put("Otros", getString(R.string.category_others));

        categoryMap.put("Food", getString(R.string.category_food));
        categoryMap.put("Transport", getString(R.string.category_transport));
        categoryMap.put("Fun", getString(R.string.category_fun));
        categoryMap.put("Home", getString(R.string.category_home));
        categoryMap.put("Others", getString(R.string.category_others));

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

            String categoryInSpanish = categoryMap.get(expense.getCategoria());
            boolean matchesCategory = selectedCategory.equals(getString(R.string.all_categories)) ||
             expense.getCategoria().equalsIgnoreCase(selectedCategory)||
                    (categoryInSpanish != null && categoryInSpanish.equalsIgnoreCase(selectedCategory));


            boolean matchesTime = true; // Default is true (if "Todos" is selected)

            try {
                Date expenseDate = dateFormat.parse(expense.getFecha());
                if (selectedTimeOption.equals(getString(R.string.time_today))) {
                    matchesTime = dateFormat.format(expenseDate).equals(dateFormat.format(today));
                } else if (selectedTimeOption.equals(getString(R.string.time_yesterday))) {
                    matchesTime = dateFormat.format(expenseDate).equals(dateFormat.format(yesterday));
                } else if (selectedTimeOption.equals(getString(R.string.time_last_week))) {
                    matchesTime = expenseDate.after(lastWeek) && !expenseDate.after(today);
                } else if (selectedTimeOption.equals(getString(R.string.time_last_month))) {
                    matchesTime = expenseDate.after(lastMonth) && !expenseDate.after(today);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (matchesSearch && matchesCategory && matchesTime) {
                filteredList.add(expense);
            }
        }
        if (selectedSortOption.equals(getString(R.string.sort_recent))) {
            Collections.sort(filteredList, (o1, o2) -> o2.getFecha().compareTo(o1.getFecha()));
        } else if (selectedSortOption.equals(getString(R.string.sort_oldest))) {
            Collections.sort(filteredList, Comparator.comparing(Gasto::getFecha));
        } else if (selectedSortOption.equals(getString(R.string.sort_expensive))) {
            Collections.sort(filteredList, (o1, o2) -> Double.compare(o2.getCantidad(), o1.getCantidad()));
        } else if (selectedSortOption.equals(getString(R.string.sort_cheap))) {
            Collections.sort(filteredList, Comparator.comparingDouble(Gasto::getCantidad));
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
        }else  if (requestCode == EDIT_EXPENSE_REQUEST_CODE  && resultCode == RESULT_OK) {
            // Actualizar lista después de añadir un gasto
            GastoDbHelper dbHelper = new GastoDbHelper(this);
            expenseList = dbHelper.obtenerGastos();
            filteredList.clear();
            filteredList.addAll(expenseList);
            adapter.notifyDataSetChanged();
        }

       updateFilteredList();
    }
    // Método para abrir los detalles del gasto
    private void abrirDetalleGasto(int gastoId) {
        Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
        intent.putExtra("id", gastoId);  // Pasamos el id del gasto a la actividad de detalles
        startActivity(intent);
    }


}
