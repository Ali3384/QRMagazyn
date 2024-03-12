package com.example.qrmagazyn;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

public class WarehouseDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public WarehouseDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        try {
            dbHelper.copyDatabaseFromAssets();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error if necessary
        }
        database = dbHelper.getReadableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public String getItemPlaceByCode(String itemCode) {
        String itemPlace = null;
        String query = "SELECT " + dbHelper.getItemPlaceColumnName() +
                " FROM " + DatabaseHelper.TABLE_WAREHOUSE +
                " WHERE " + dbHelper.getItemCodeColumnName() + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{itemCode});
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(dbHelper.getItemPlaceColumnName());
            if (columnIndex >= 0) { // Check if column index is valid
                itemPlace = cursor.getString(columnIndex);
            } else {
                // Handle the case where the column index is -1
                // Log an error message or throw an exception if necessary
            }
            cursor.close();
        }
        return itemPlace;
    }
}