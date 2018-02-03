package com.example.devsmar.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devsmar.myapplication.data.DataContract;

/**
 * Created by Dev Smar on 1/22/2018.
 */

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.list_name);
        TextView quantityTextView = view.findViewById(R.id.list_quantity);
        TextView priceTextView = view.findViewById(R.id.list_price);
        ImageView imageView = view.findViewById(R.id.list_image);

        String name = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_NAME));
        String price = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_PRICE));
        String quantity = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_QUANTITY));

        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_IMAGE)));
        if (uri == null) {
            Log.i("InventoryAdapter","Failed to get image with uri " + uri);
        }
        else {
            Log.i("InventoryAdapter","Successfully getting image with uri " + uri);
            imageView.setImageURI(uri);
        }
        nameTextView.setText(name);
        quantityTextView.setText("X"+quantity);
        priceTextView.setText("₹"+price);
    }
}
