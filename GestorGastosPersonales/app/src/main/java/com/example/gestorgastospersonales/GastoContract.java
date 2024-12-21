package com.example.gestorgastospersonales;

import android.provider.BaseColumns;

public final class GastoContract {
    private GastoContract() {}

    public static abstract class GastoEntry implements BaseColumns {
        // Nombre de la tabla
        public static final String TABLE_NAME = "Gastos";

        // Columnas de la tabla
        public static final String COLUMN_NAME_FECHA = "fecha";
        public static final String COLUMN_NAME_LUGAR = "lugar";
        public static final String COLUMN_NAME_DESCRIPCION = "descripcion";
        public static final String COLUMN_NAME_CATEGORIA = "categoria";
        public static final String COLUMN_NAME_CANTIDAD = "cantidad";

        // Categorías predefinidas
        public static final String CATEGORIA_ALIMENTACION = "Comida";
        public static final String CATEGORIA_TRANSPORTE = "Transporte";
        public static final String CATEGORIA_ENTRETENIMIENTO = "Ocio";
        public static final String CATEGORIA_VIVIENDA = "Vivienda";
        public static final String CATEGORIA_OTROS = "Otros";
    }
}
