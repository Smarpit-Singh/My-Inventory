package com.example.devsmar.myapplication;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.devsmar.myapplication.data.DataContract;

public class ShowListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID1 = 1;
    InventoryAdapter inventoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ID1,null,this);

        ListView listView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        inventoryAdapter = new InventoryAdapter(this, null);
        listView.setAdapter(inventoryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowListActivity.this, DetailActivity.class);
                Uri currentUri = ContentUris.withAppendedId(DataContract.ItemName.CONTENT_URI, id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuadd:
                addInventorySample();
                return true;
                
            case R.id.menudeleteall:
                deleteAllItems();
                return true;
                
            case R.id.menuAbout:
                String url = "https://github.com/Smarpit-Singh";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        int result = getContentResolver().delete(DataContract.ItemName.CONTENT_URI,null,null);
        makeLog(result + " row deleted.");
    }

    private void addInventorySample() {
        ContentValues values = new ContentValues();
        values.put(DataContract.ItemName.COLUMN_NAME,"iphone x");
        values.put(DataContract.ItemName.COLUMN_PRICE,"44");
        values.put(DataContract.ItemName.COLUMN_QUANTITY,"12");
        values.put(DataContract.ItemName.COLUMN_SUPPLIER_NAME,"Smarpit");
        values.put(DataContract.ItemName.COLUMN_SUPPLIER_PHONE,"82839433058");
        values.put(DataContract.ItemName.COLUMN_SUPPLIER_EMAIL,"supplier-email@gmail.com");
        values.put(DataContract.ItemName.COLUMN_SUPPLIER_ADDRESS,"vpo ballowal");
        values.put(DataContract.ItemName.COLUMN_BARCODE,"876865675765");
        values.put(DataContract.ItemName.COLUMN_DATE,"2018/01/29");
        values.put(DataContract.ItemName.COLUMN_IMAGE,"android.resource://com.example.devsmar.myapplication/drawable/gummibear");

        Uri result = getContentResolver().insert(DataContract.ItemName.CONTENT_URI, values);

        if (result == null){
            makeLog("There is error while saving Inventory");
        }

        else
        {
            makeLog("Saved successfully");
        }

    }

    private void makeLog(String s) {
        Log.i("ShowListActivity",s);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String projection[] = {
                DataContract.ItemName._ID,
                DataContract.ItemName.COLUMN_NAME,
                DataContract.ItemName.COLUMN_QUANTITY,
                DataContract.ItemName.COLUMN_PRICE,
                DataContract.ItemName.COLUMN_IMAGE };

        return new CursorLoader(
                this,
                DataContract.ItemName.CONTENT_URI,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryAdapter.swapCursor(null);
    }
}
