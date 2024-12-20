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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addExpenseButton;
    private ExpenseAdapter adapter;
    private EditText searchEditText;
    private Spinner categorySpinner, sortSpinner;
    private List<Expense> expenseList;
    private List<Expense> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        sortSpinner = findViewById(R.id.sortSpinner);

        expenseList = DBHelper.getAllExpenses(this);
        filteredList = new ArrayList<>(expenseList);

        adapter = new ExpenseAdapter(this, filteredList);
        listView.setAdapter(adapter);

//        // Configurar el botón para añadir un nuevo gasto
//        addExpenseButton.setOnClickListener(v -> {
//            // Acción para abrir la actividad de añadir gasto
//        });

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

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
//                Expense selectedExpense = filteredList.get(position);
//                Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
//                intent.putExtra("expense", selectedExpense);
//                startActivity(intent);
//            }
//        });
    }

    private void updateFilteredList() {
        String searchQuery = searchEditText.getText().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        String selectedSort = sortSpinner.getSelectedItem().toString();

        String[] sortOptions = getResources().getStringArray(R.array.sort_options);
        String[] categories = getResources().getStringArray(R.array.categories);

        String allCategories = categories[0];

        filteredList.clear();

        for (Expense expense : expenseList) {
            if (!expense.getTitle().toLowerCase().contains(searchQuery)) {
                continue;
            }

            if (!selectedCategory.equals(allCategories) && !expense.getCategory().equals(selectedCategory)) {
                continue;
            }

            filteredList.add(expense);
        }

        if (selectedSort.equals(sortOptions[0])) { // Más recientes / Newest
            filteredList.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
        } else if (selectedSort.equals(sortOptions[1])) { // Más antiguos / Oldest
            filteredList.sort(Comparator.comparing(Expense::getDate));
        } else if (selectedSort.equals(sortOptions[2])) { // Más caros / Most expensive
            filteredList.sort((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
        } else if (selectedSort.equals(sortOptions[3])) { // Más baratos / Cheapest
            filteredList.sort(Comparator.comparingDouble(Expense::getAmount));
        }

        // Notificar cambios al adaptador
        adapter.notifyDataSetChanged();
    }



}
