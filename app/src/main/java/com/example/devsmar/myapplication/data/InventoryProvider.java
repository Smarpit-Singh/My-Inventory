package com.example.devsmar.myapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.devsmar.myapplication.data.StockDBHelper.LOG_TAG;

/**
 * Created by Dev Smar on 1/11/2018.
 */

public class InventoryProvider extends ContentProvider {

    private static final String TAG = InventoryProvider.class.getSimpleName();
    public static final int INVENTORIES = 100;
    public static final int INVENTORY_ID = 101;

    public static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.BASE_PATH, INVENTORIES);
        mUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.BASE_PATH + "/#", INVENTORY_ID);
    }

    StockDBHelper stockDBHelper;

    @Override
    public boolean onCreate() {
        stockDBHelper = new StockDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String order) {
        SQLiteDatabase sqLiteDatabase = stockDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = mUriMatcher.match(uri);
        switch (match) {
            case INVENTORIES:
                cursor = sqLiteDatabase.query(DataContract.ItemName.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, order);
                break;

            case INVENTORY_ID:

                selection = DataContract.ItemName._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(DataContract.ItemName.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, order);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = mUriMatcher.match(uri);
        switch (match){
            case INVENTORIES:
                return DataContract.ItemName.CONTENT_DIR_TYPE;
            case INVENTORY_ID:
                return DataContract.ItemName.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown uri "+uri+" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = stockDBHelper.getWritableDatabase();

        switch (match){
            case INVENTORIES:

               return insertInventory(uri , contentValues, db);
                default:
                    throw new IllegalArgumentException("Insertion in not supported for "+uri);

        }
    }

    private Uri insertInventory(Uri uri, ContentValues contentValues, SQLiteDatabase db) {
        long id = db.insert(DataContract.ItemName.TABLE_NAME,null,contentValues);

        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArg) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = stockDBHelper.getWritableDatabase();
        int rowDeleted;
        switch (match){
            case INVENTORIES:
                rowDeleted = db.delete(DataContract.ItemName.TABLE_NAME, selection, selectionArg);
                break;

            case INVENTORY_ID:
                selection = DataContract.ItemName._ID + "=?";
                selectionArg = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = db.delete(DataContract.ItemName.TABLE_NAME,selection,selectionArg);
                break;

                default:
                    throw new IllegalArgumentException("Deletion is not supported for this uri : "+uri);
        }

        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = stockDBHelper.getWritableDatabase();
        switch (match){
            case INVENTORIES:
                return updateInventory(uri , contentValues, selection, selectionArgs, db);

            case INVENTORY_ID:
                selection = DataContract.ItemName._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs, db);

                default:
                    throw new IllegalArgumentException("Update is not support for this uri : "+uri);
        }
    }

    private int updateInventory(Uri uri,ContentValues values, String selection, String[] selectionArgs, SQLiteDatabase db) {
        int rowUpdated = db.update(DataContract.ItemName.TABLE_NAME,values,selection,selectionArgs);

        if (rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdated;
    }
}
