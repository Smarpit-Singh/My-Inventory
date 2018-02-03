package com.example.devsmar.myapplication;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.devsmar.myapplication.data.DataContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOAGER_ID2 = 2;
    Uri currentUri;

    TextView name, price, quantity, s_name, s_phone, s_email, s_address, barcode, date;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        currentUri = intent.getData();

        initializeUs();

        getLoaderManager().initLoader(LOAGER_ID2, null, this);
    }

    private void initializeUs() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.content_name);
        price = findViewById(R.id.content_price);
        quantity = findViewById(R.id.content_quantity);
        s_name = findViewById(R.id.content_supplier_nameText);
        s_phone = findViewById(R.id.content_supplier_phoneText);
        s_email = findViewById(R.id.content_supplier_emailText);
        s_address = findViewById(R.id.content_supplier_addressText);
        barcode = findViewById(R.id.content_barcode);
        date = findViewById(R.id.content_date);
        imageView = findViewById(R.id.content_pic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuDeleteThis:
                deleteThis();
                return true;

            case R.id.menuEdit:
                Intent intent = new Intent(DetailActivity.this, InsertActivity.class);
                intent.setData(currentUri);
                startActivity(intent);
                return true;

            case R.id.menuOrderIt:
                showOrderConfirmationDialog();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteThis() {
       int  result = getContentResolver().delete(currentUri,null,null);

       if (result != -1){
           Toast.makeText(this, "Successfully deleted", Toast.LENGTH_SHORT).show();
           finish();
       }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                DataContract.ItemName._ID,
                DataContract.ItemName.COLUMN_NAME,
                DataContract.ItemName.COLUMN_PRICE,
                DataContract.ItemName.COLUMN_QUANTITY,
                DataContract.ItemName.COLUMN_SUPPLIER_NAME,
                DataContract.ItemName.COLUMN_SUPPLIER_PHONE,
                DataContract.ItemName.COLUMN_SUPPLIER_EMAIL,
                DataContract.ItemName.COLUMN_SUPPLIER_ADDRESS,
                DataContract.ItemName.COLUMN_BARCODE,
                DataContract.ItemName.COLUMN_DATE,
                DataContract.ItemName.COLUMN_IMAGE };

        return new CursorLoader(
                this,
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()){
            String nameText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_NAME));
            String priceText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_PRICE));
            String quantityText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_QUANTITY));
            String s_nameText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_NAME));
            String s_phoneText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_PHONE));
            String s_emailText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_EMAIL));
            String s_addressText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_ADDRESS));
            String barcodeText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_BARCODE));
            String dateText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_DATE));
            String imageUriText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_IMAGE));

            name.setText(nameText);
            price.setText(priceText);
            quantity.setText(quantityText);
            s_name.setText(s_nameText);
            s_phone.setText(s_phoneText);
            s_email.setText(s_emailText);
            s_address.setText(s_addressText);
            barcode.setText(barcodeText);
            imageView.setImageURI(Uri.parse(imageUriText));
            date.setText(dateText);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        price.setText("");
        quantity.setText("");
        s_name.setText("");
        s_phone.setText("");
        s_email.setText("");
        s_address.setText("");
        barcode.setText("");
        date.setText("");
        imageView.setImageURI(null);
    }

    private void showOrderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You can place an order for this item by phone or e-mail");
        builder.setPositiveButton("Phone", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + s_phone.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Email", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + s_email.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Recurrent new order");
                String bodyMessage = "Please send us as soon as possible more " +
                        name.getText().toString().trim() +
                        "!!!";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

