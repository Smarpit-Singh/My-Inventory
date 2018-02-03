package com.example.devsmar.myapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dev Smar on 1/17/2018.
 */

public final class DataContract {

    private DataContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.devsmar.myapplication";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String BASE_PATH = "stock";

    public static final class ItemName implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, BASE_PATH);

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + BASE_PATH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + "/" + BASE_PATH;

        public static final String TABLE_NAME = "stock";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_SUPPLIER_ADDRESS = "supplier_address";
        public static final String COLUMN_BARCODE = "barcode";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_STOCK = "CREATE TABLE "
                + TABLE_NAME + "( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + COLUMN_NAME + " TEXT NOT NULL ,"
                + COLUMN_DATE + " TEXT NOT NULL ,"
                + COLUMN_BARCODE + " TEXT NOT NULL ,"
                + COLUMN_PRICE + " TEXT NOT NULL ,"
                + COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0 ,"
                + COLUMN_SUPPLIER_NAME + " TEXT NOT NULL ,"
                + COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL ,"
                + COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL ,"
                + COLUMN_SUPPLIER_ADDRESS + " TEXT NOT NULL ,"
                + COLUMN_IMAGE + " TEXT NOT NULL );";
    }

}
