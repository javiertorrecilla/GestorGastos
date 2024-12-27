package com.example.gestorgastospersonales;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExpenseAdapter extends BaseAdapter {

    private int EDIT_EXPENSE_REQUEST_CODE = 2;
    private Context context;
    private List<Gasto> expenseList;
    private GastoDbHelper dbHelper;

    public ExpenseAdapter(Context context, List<Gasto> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        this.dbHelper = new GastoDbHelper(context); // Inicializa el helper para operaciones en la base de datos
    }

    @Override
    public int getCount() {
        return expenseList.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        }

        Gasto expense = expenseList.get(position);

        // Configurar los textos
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);

        titleTextView.setText(expense.getLugar());
        descriptionTextView.setText(expense.getDescripcion());
        dateTextView.setText(expense.getFecha());
        amountTextView.setText(String.format("€%.2f", expense.getCantidad()));
        categoryTextView.setText(expense.getCategoria());

        // Botón de eliminar
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            // Mostrar el AlertDialog de confirmación
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar Gasto")
                    .setMessage("¿Estás seguro de que deseas eliminar este gasto?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Eliminar el gasto de la base de datos
                        dbHelper.eliminarGasto(expense);

                        // Eliminar el gasto de la lista y actualizar la vista
                        expenseList.remove(position);
                        notifyDataSetChanged();

                        // Mostrar mensaje de confirmación
                        Toast.makeText(context, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
        Button editButton = convertView.findViewById(R.id.editButton);
        // Acción del botón Editar
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("gastoId", expense.getId()); // Pasa el ID del gasto
            intent.putExtra("cantidad", expense.getCantidad()); // Cambia por los valores reales
            intent.putExtra("categoria", expense.getCategoria());
            intent.putExtra("fecha", expense.getFecha());
            intent.putExtra("descripcion", expense.getDescripcion());
            intent.putExtra("lugar",expense.getLugar());
            context.startActivity(intent);
        });

        return convertView;
    }
}
