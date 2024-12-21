package com.example.gestorgastospersonales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class GastoDbHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "gastos.db";
    private static final int DATABASE_VERSION = 1;

    private static boolean isFirstRun = true;  // Flag para verificar si es la primera ejecución

    public GastoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla
        String CREATE_GASTOS_TABLE = "CREATE TABLE " + GastoContract.GastoEntry.TABLE_NAME + " ("
                + GastoContract.GastoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GastoContract.GastoEntry.COLUMN_NAME_FECHA + " TEXT, "
                + GastoContract.GastoEntry.COLUMN_NAME_LUGAR + " TEXT, "
                + GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION + " TEXT, "
                + GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA + " TEXT, "
                + GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD + " REAL)";
        db.execSQL(CREATE_GASTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GastoContract.GastoEntry.TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar un gasto
    public long insertarGasto(Gasto gasto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GastoContract.GastoEntry.COLUMN_NAME_FECHA, gasto.getFecha());
        values.put(GastoContract.GastoEntry.COLUMN_NAME_LUGAR, gasto.getLugar());
        values.put(GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION, gasto.getDescripcion());
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA, gasto.getCategoria());
        values.put(GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD, gasto.getCantidad());

        long id = db.insert(GastoContract.GastoEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Método para obtener todos los gastos
    public List<Gasto> obtenerGastos() {
        List<Gasto> gastos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columnas = {
                GastoContract.GastoEntry._ID,
                GastoContract.GastoEntry.COLUMN_NAME_FECHA,
                GastoContract.GastoEntry.COLUMN_NAME_LUGAR,
                GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION,
                GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA,
                GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD
        };

        Cursor cursor = db.query(GastoContract.GastoEntry.TABLE_NAME, columnas, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(GastoContract.GastoEntry._ID));
            String fecha = cursor.getString(cursor.getColumnIndex(GastoContract.GastoEntry.COLUMN_NAME_FECHA));
            String lugar = cursor.getString(cursor.getColumnIndex(GastoContract.GastoEntry.COLUMN_NAME_LUGAR));
            String descripcion = cursor.getString(cursor.getColumnIndex(GastoContract.GastoEntry.COLUMN_NAME_DESCRIPCION));
            String categoria = cursor.getString(cursor.getColumnIndex(GastoContract.GastoEntry.COLUMN_NAME_CATEGORIA));
            double cantidad = cursor.getDouble(cursor.getColumnIndex(GastoContract.GastoEntry.COLUMN_NAME_CANTIDAD));

            Gasto gasto = new Gasto(id, fecha, lugar, descripcion, categoria, cantidad);
            gastos.add(gasto);
        }
        cursor.close();
        db.close();
        return gastos;
    }

    // Método para inicializar los gastos predeterminados
    public void initGastos(Context context) {
        if (isFirstRun) {
            SQLiteDatabase db = this.getWritableDatabase();
            Gasto gasto1 = new Gasto(1,"2024-12-21", "Supermercado", "Compra de alimentos", GastoContract.GastoEntry.CATEGORIA_ALIMENTACION, 25.50);
            Gasto gasto2 = new Gasto(2,"2024-12-20", "Estación de tren", "Billete de tren", GastoContract.GastoEntry.CATEGORIA_TRANSPORTE, 15.00);
            Gasto gasto3 = new Gasto(3,"2024-12-19", "Cine", "Entrada para película", GastoContract.GastoEntry.CATEGORIA_ENTRETENIMIENTO, 10.00);
            Gasto gasto4 = new Gasto(4,"2024-12-18", "Alquiler", "Pago mensual alquiler", GastoContract.GastoEntry.CATEGORIA_VIVIENDA, 400.00);
            Gasto gasto5 = new Gasto(5,"2024-12-17", "Varios", "Otros gastos", GastoContract.GastoEntry.CATEGORIA_OTROS, 5.00);

            insertarGasto(gasto1);
            insertarGasto(gasto2);
            insertarGasto(gasto3);
            insertarGasto(gasto4);
            insertarGasto(gasto5);

            isFirstRun = false;  // Evita que se vuelvan a insertar los gastos predeterminados
        }
    }

    public void borrarDatos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GastoContract.GastoEntry.TABLE_NAME, null, null);  // Borra todos los registros
        db.close();
    }

}
