package com.example.gestorgastospersonales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private List<Expense> expenseList;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
        }

        // Obtener el gasto en la posición actual
        Expense expense = expenseList.get(position);

        // Enlazar los elementos de la vista con los datos
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView conceptTextView = convertView.findViewById(R.id.conceptTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);

        titleTextView.setText(expense.getTitle());
        conceptTextView.setText(expense.getConcept());
        dateTextView.setText(expense.getDate());
        amountTextView.setText(String.format("$%.2f", expense.getAmount())); // Formato de cantidad

        return convertView;
    }
}
