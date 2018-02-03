package com.example.devsmar.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dev Smar on 1/8/2018.
 */

public class StockDBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "stock.db";
    public static final int DB_VERSION = 1;
    public static final String LOG_TAG = SQLiteOpenHelper.class.getCanonicalName();

    public StockDBHelper(Context context) {
        super(context, DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataContract.ItemName.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
