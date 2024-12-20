package com.example.gestorgastospersonales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addExpenseButton;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addExpenseButton = findViewById(R.id.addExpenseButton);

        // Mostrar lista de gastos
        adapter = new ExpenseAdapter(this, DBHelper.getAllExpenses(this));
        listView.setAdapter(adapter);

//        // Configurar el listener para seleccionar un gasto
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Obtener el gasto seleccionado
//                Expense selectedExpense = (Expense) parent.getItemAtPosition(position);
//
//                // Crear el Intent para pasar a la actividad de detalles
//                Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
//                intent.putExtra("expense", selectedExpense); // Pasar el gasto seleccionado
//                startActivity(intent);
//            }
//        });

//        // Configurar el listener para el botón de añadir gasto
//        addExpenseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Abrir la actividad para añadir un nuevo gasto
//                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
