package com.example.project.Utilidades;

public class Utilidades {
    public static final String TABLA_PRODUCTO = "Producto";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_MARCA = "marca";
    public static final String CAMPO_PESO_INICIAL = "pesoinicial";
    public static final String CAMPO_PESO_ACTUAL = "pesoactual";
    public static final String CAMPO_CADUCIDAD = "caducidad";

    public static final String CREAR_TABLA_PRODUCTO = "CREATE TABLE " +TABLA_PRODUCTO+
            " ( " +CAMPO_ID+ " INTEGER PRIMARY KEY,"
            +CAMPO_NOMBRE+ " TEXT, "
            +CAMPO_MARCA+ " TEXT, "
            +CAMPO_PESO_INICIAL+ " TEXT, "
            +CAMPO_PESO_ACTUAL+ " TEXT, "
            +CAMPO_CADUCIDAD+ " TEXT )";

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
