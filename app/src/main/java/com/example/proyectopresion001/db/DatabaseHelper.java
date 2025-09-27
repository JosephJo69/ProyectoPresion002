package com.example.proyectopresion001.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PresionDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REGISTROS = "registros";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SISTOLICA = "sistolica";
    public static final String COLUMN_DIASTOLICA = "diastolica";
    public static final String COLUMN_EDAD = "edad";
    public static final String COLUMN_FECHA = "fecha";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_REGISTROS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_SISTOLICA + " integer not null, "
            + COLUMN_DIASTOLICA + " integer, "
            + COLUMN_EDAD + " integer, "
            + COLUMN_FECHA + " datetime default current_timestamp);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTROS);
        onCreate(db);
    }

    public long insertRegistro(int sistolica, Integer diastolica, Integer edad) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SISTOLICA, sistolica);
        if (diastolica != null) values.put(COLUMN_DIASTOLICA, diastolica);
        if (edad != null) values.put(COLUMN_EDAD, edad);
        return db.insert(TABLE_REGISTROS, null, values);
    }

    public List<Registro> getAllRegistros() {
        List<Registro> registros = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REGISTROS, null, null, null, null, null, COLUMN_FECHA + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Registro registro = new Registro();
                registro.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                registro.setSistolica(cursor.getInt(cursor.getColumnIndex(COLUMN_SISTOLICA)));
                registro.setDiastolica(cursor.getInt(cursor.getColumnIndex(COLUMN_DIASTOLICA)));
                registro.setEdad(cursor.getInt(cursor.getColumnIndex(COLUMN_EDAD)));
                registro.setFecha(cursor.getString(cursor.getColumnIndex(COLUMN_FECHA)));
                registros.add(registro);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return registros;
    }
}
