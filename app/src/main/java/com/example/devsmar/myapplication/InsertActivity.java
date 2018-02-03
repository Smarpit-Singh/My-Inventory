package com.example.devsmar.myapplication;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.devsmar.myapplication.data.DataContract;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public Uri imageUri;
    private static final int LOADER_ID3 = 3;

    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;

    public boolean isInventoryChanged = false;

    EditText name, price, quantity, s_name, s_phone, s_email, s_address, barcodeText;
    Button buttonBarcode, buttonImage;
    ImageView image;
    Uri currentUri;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            isInventoryChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        initialize();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        Intent intent = getIntent();
        currentUri = intent.getData();

        if (currentUri != null) {
            setTitle("Edit Inventory");
            getLoaderManager().initLoader(LOADER_ID3, null, this);
        } else {
            setTitle("Save Inventory");
        }

        buttonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBarcode();
            }
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InsertActivity.this);
                builder.setTitle("Insert Image");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromCamera();
                    }
                });
                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromGallery();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void initialize() {
        name = findViewById(R.id.nameText);
        price = findViewById(R.id.priceText);
        quantity = findViewById(R.id.quantityText);
        s_name = findViewById(R.id.supplier_nameText);
        s_phone = findViewById(R.id.supplier_phoneText);
        s_email = findViewById(R.id.supplier_emailText);
        s_address = findViewById(R.id.supplier_addressText);
        barcodeText = findViewById(R.id.supplier_barcodeText);

        buttonBarcode = findViewById(R.id.barcodeButton);
        buttonImage = findViewById(R.id.selectimageButton);

        image = findViewById(R.id.insertImage);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        name.setOnTouchListener(mTouchListener);
        price.setOnTouchListener(mTouchListener);
        quantity.setOnTouchListener(mTouchListener);
        s_name.setOnTouchListener(mTouchListener);
        s_phone.setOnTouchListener(mTouchListener);
        s_email.setOnTouchListener(mTouchListener);
        s_address.setOnTouchListener(mTouchListener);
        barcodeText.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editorSaveMenu:
                saveInventory();
                return true;

            case R.id.editorCancelMenu:
                finish();
                return true;

            case R.id.home:
                if (!isInventoryChanged) {
                    NavUtils.navigateUpFromSameTask(InsertActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(InsertActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveInventory() {

        boolean isOk = true;

        if (!sanityCheck(name, "name")) {
            isOk = false;
        }
        if (!sanityCheck(price, "price")) {
            isOk = false;
        }
        if (!sanityCheck(quantity, "quantity")) {
            isOk = false;
        }
        if (!sanityCheck(s_name, "Supplier Name")) {
            isOk = false;
        }
        if (!sanityCheck(s_phone, "Supplier Phone")) {
            isOk = false;
        }
        if (!sanityCheck(s_email, "Supplier Email")) {
            isOk = false;
        }
        if (!sanityCheck(s_address, "Supplier Address")) {
            isOk = false;
        }
        if (!sanityCheck(barcodeText, "Barcode")) {
            isOk = false;
        }
        if (imageUri == null) {
            isOk = false;
            buttonImage.setError("Select Image");
        }
        if (isOk) {

            ContentValues values = new ContentValues();
            values.put(DataContract.ItemName.COLUMN_NAME, name.getText().toString());
            values.put(DataContract.ItemName.COLUMN_PRICE, price.getText().toString());
            values.put(DataContract.ItemName.COLUMN_QUANTITY, quantity.getText().toString());
            values.put(DataContract.ItemName.COLUMN_SUPPLIER_NAME, s_name.getText().toString());
            values.put(DataContract.ItemName.COLUMN_SUPPLIER_PHONE, s_phone.getText().toString());
            values.put(DataContract.ItemName.COLUMN_SUPPLIER_EMAIL, s_email.getText().toString());
            values.put(DataContract.ItemName.COLUMN_SUPPLIER_ADDRESS, s_address.getText().toString());
            values.put(DataContract.ItemName.COLUMN_BARCODE, barcodeText.getText().toString());
            values.put(DataContract.ItemName.COLUMN_IMAGE, imageUri.toString());

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String timeStamp = df.format(c.getTime());
            values.put(DataContract.ItemName.COLUMN_DATE, timeStamp);

            if (currentUri == null) {

                Uri uri = getContentResolver().insert(DataContract.ItemName.CONTENT_URI, values);

                if (uri == null) {
                    makeToast("Not saved");
                } else {
                    makeToast("Saved");
                    finish();
                }

            } else {
                String selection = DataContract.ItemName._ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(currentUri))};
                int result = getContentResolver().update(DataContract.ItemName.CONTENT_URI, values, selection, selectionArgs);

                if (result != -1)
                    makeToast("Updated");
                finish();
            }
        }
    }

    public boolean sanityCheck(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    public void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void getBarcode() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        Intent intent = new Intent(InsertActivity.this, BarcodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    public void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && data != null) {
                    Uri result = data.getData();
                    imageUri = result;
                    image.setImageResource(0);
                    image.setImageDrawable(null);
                    image.setImageURI(result);
                }
                break;

            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    image.setImageResource(0);
                    image.setImageDrawable(null);
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    image.setImageBitmap(photo);
                    String result = saveToInternalStorage(photo);
                    imageUri = Uri.parse(result);
                }
                break;

            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        final Barcode barcode = data.getParcelableExtra("barcode");
                        barcodeText.setText(barcode.displayValue);
                    }
                    break;
                }
        }
    }


    private String saveToInternalStorage(Bitmap bitmapImage) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Create imageDir
        File mypath = new File(directory, imageFileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 85, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath() + "/" + imageFileName;
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
                DataContract.ItemName.COLUMN_IMAGE};

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

        if (cursor.moveToFirst()) {
            String nameText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_NAME));
            String priceText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_PRICE));
            String quantityText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_QUANTITY));
            String s_nameText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_NAME));
            String s_phoneText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_PHONE));
            String s_emailText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_EMAIL));
            String s_addressText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_SUPPLIER_ADDRESS));
            String barcodeText1 = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_BARCODE));
            String imageUriText = cursor.getString(cursor.getColumnIndex(DataContract.ItemName.COLUMN_IMAGE));

            name.setText(nameText);
            price.setText(priceText);
            quantity.setText(quantityText);
            s_name.setText(s_nameText);
            s_phone.setText(s_phoneText);
            s_email.setText(s_emailText);
            s_address.setText(s_addressText);
            barcodeText.setText(barcodeText1);

            imageUri = Uri.parse(imageUriText);
            image.setImageURI(imageUri);
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
        barcodeText.setText("");
        image.setImageURI(null);
    }

    @Override
    public void onBackPressed() {

        if (!isInventoryChanged) {
            super.onBackPressed();
            return;
        }


        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Disgard your changes and quit editing");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
