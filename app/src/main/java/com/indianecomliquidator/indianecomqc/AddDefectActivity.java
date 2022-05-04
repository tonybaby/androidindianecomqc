package com.indianecomliquidator.indianecomqc;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AddDefectActivity extends AppCompatActivity {
    AlertDialog alertDialogPleaseWait;
    String hostAddress;
    int count = 0;
    RequestQueue requestQueue;
    String itemDefectId, selectedDefectTypeId;
    EditText editTextComment;
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    Integer CAMERA_IMAGE = 1, GALLARY_IMAGE = 2;
    Uri imageUri;

    String currentPhotoPath;
    Integer REQUEST_IMAGE_CAPTURE = 1;
    Uri photoURI;

    String defectTypes;
    String tempDefectImageFolder;

    public static List<StoreDetailsDefectImages> listArrayRecyclerView;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagerRecyclerView;
    private static AdapterRecyclerviewDefectImages adapterRecyclerviewDefectImages;
    int dpWidth, dpHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_defect);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Defect");

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        dpWidth = size.x;
        dpHeight = size.y;

        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        SharedPreferences sharedPreferencesAppDetails = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
        itemDefectId = sharedPreferencesAppDetails.getString("item_defect_id", "");
        defectTypes = sharedPreferencesAppDetails.getString("defect_type_list", "");

        tempDefectImageFolder = UUID.randomUUID().toString();
        SharedPreferences.Editor editor = sharedPreferencesAppDetails.edit();
        editor.putString("temp_defect_image_folder", tempDefectImageFolder);
        editor.commit();

//        EditText editTextProductName = (EditText) findViewById(R.id.editTextProductName);
//        editTextProductName.setText(qc_selected_product_name);
//        editTextProductName.setEnabled(false);

        ArrayList<String> arrayListDefectType = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(defectTypes);
            if (jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = null;
                    json = jsonArray.getJSONObject(i);

                    arrayListDefectType.add(json.getString("name"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Spinner spinnerDefectType = (Spinner) findViewById(R.id.spinnerProductType);
        ArrayAdapter<String> adapterProductStatus = new ArrayAdapter<String>(AddDefectActivity.this, android.R.layout.simple_spinner_item, arrayListDefectType);
        adapterProductStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDefectType.setAdapter(adapterProductStatus);
        spinnerDefectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDefectType = (String) spinnerDefectType.getSelectedItem();
                if(selectedDefectType.equals("Functional")){
                    selectedDefectTypeId = "1";
                }else if(selectedDefectType.equals("Physical")){
                    selectedDefectTypeId = "2";
                }else{
                    selectedDefectTypeId = "3";
                }
                Log.i("location", selectedDefectType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextComment = (EditText) findViewById(R.id.editTextComment);

        requestQueue = Volley.newRequestQueue(this);

//        someActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            // There are no request codes
//                            Intent data = result.getData();
//                            Log.i("location", data + "data");
//                        }
//                    }
//                });

        listArrayRecyclerView = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManagerRecyclerView = new GridLayoutManager(AddDefectActivity.this, 2);
        recyclerView.setLayoutManager(layoutManagerRecyclerView);
        adapterRecyclerviewDefectImages = new AdapterRecyclerviewDefectImages(listArrayRecyclerView,AddDefectActivity.this, dpHeight, dpWidth);
        recyclerView.setAdapter(adapterRecyclerviewDefectImages);
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void toAddImage(final View view) {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(AddDefectActivity.this);
        LayoutInflater inflater = AddDefectActivity.this.getLayoutInflater();
        final View viewDialog=inflater.inflate(R.layout.dialog_image_selector, null);
        alertDialogBuilder.setView(viewDialog);
        final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
        textViewMessageHead.setText("SELECT AN OPTION");
        Button button1 = (Button) viewDialog.findViewById(R.id.button1);
        button1.setText("OPEN GALLERY");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 2);
            }
        });
        Button button2 = (Button) viewDialog.findViewById(R.id.button2);
        button2.setText("OPEN CAMERA");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

                try {
                    createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String uniqueString = UUID.randomUUID().toString();
                dispatchTakePictureIntent(uniqueString);
            }
        });

    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (reqCode == GALLARY_IMAGE) {
                try {
                    imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    SharedPreferences pref = AddDefectActivity.this.getSharedPreferences("AppDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("selected_defect_image_uri", String.valueOf(imageUri));
                    editor.commit();
                    Intent intent = new Intent(AddDefectActivity.this, DrawTextOnImgActivity.class);
                    startActivity(intent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AddDefectActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (reqCode == REQUEST_IMAGE_CAPTURE) {
                Log.i("location", String.valueOf(photoURI) + "hi");
                Bitmap selectedImage = null;
                try {
                    selectedImage = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(photoURI), null, null);

                    SharedPreferences pref = AddDefectActivity.this.getSharedPreferences("AppDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("selected_defect_image_uri", String.valueOf(photoURI));
                    editor.commit();
                    Intent intent = new Intent(AddDefectActivity.this, DrawTextOnImgActivity.class);
                    startActivity(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.i("location", e + "hi");
                }
            }

        }else {
            Toast.makeText(AddDefectActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = AddDefectActivity.this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Indianecomqc/" + UUID.randomUUID().toString() + "/");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "Indianecomqc";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        onBackPressed();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(String name) {
        Log.i("location", "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.i("location", "dispatchTakePictureIntent Q");
                    ContentResolver resolver = AddDefectActivity.this.getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Indianecomqc/");
                    photoURI = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Log.i("location", "hai");
                    photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    Log.i("location", photoURI + "hai");
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    public void saveDefect(final View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = AddDefectActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "defects/store",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("location", "Inside onResponse" + response);

//                        try {
//                            JSONObject json = new JSONObject(response);
//
//                            SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = pref.edit();
//                            editor.putString("user_id", json.getString("user_id"));
//                            editor.putString("user_name", json.getString("user_name"));
//                            editor.putString("user_email", json.getString("user_email"));
//                            editor.putString("token", json.getString("token"));
//                            editor.commit();
//
//                            pref = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
//                            editor = pref.edit();
//                            editor.putString("brand_list", json.getString("brand_list"));
//                            editor.putString("item_list", json.getString("item_list"));
//                            editor.putString("meta_tag_list", json.getString("meta_tag_list"));
//                            editor.commit();
//
//                            Log.i("location home", json.getString("token") + "," +  json.getString("brand_list"));
//
//                            finish();
//                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//                            startActivity(i);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                        Log.i("location", "Inside onErrorResponse" + error);

                        if (count < 2) {
                            count++;

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }
                            try {
                                saveDefect(view);
                            } catch (Exception e) {
                            }
                        } else {

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(AddDefectActivity.this);
                            LayoutInflater inflater = AddDefectActivity.this.getLayoutInflater();
                            final View viewDialog=inflater.inflate(R.layout.dialog_2_buttons, null);
                            alertDialogBuilder.setView(viewDialog);
                            final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(false);
                            TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
                            textViewMessageHead.setText("MESSAGE");
                            TextView textViewMessage=(TextView)viewDialog.findViewById(R.id.textViewMessage);
                            textViewMessage.setText("Something went wrong. Please check your internet connection or try again later.");
                            Button button1 = (Button) viewDialog.findViewById(R.id.button1);
                            button1.setText("RELOAD");
                            button1.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v)
                                {
                                    try {
                                        alertDialog.cancel();
                                    } catch (Exception e){}
                                    saveDefect(view);
                                }
                            });
                            Button button2 = (Button) viewDialog.findViewById(R.id.button2);
                            button2.setText("CLOSE");
                            button2.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v)
                                {
                                    try {
                                        alertDialog.cancel();
                                    } catch (Exception e){}
                                }
                            });
                        }

                    }
                }) {

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String,String> headers = new HashMap<>();
//                headers.put("Content-Type","application/x-www-form-urlencoded");
//                return headers;
//            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferencesUserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                final String token = sharedPreferencesUserDetails.getString("token", "");

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.i("location", itemDefectId + "," + selectedDefectTypeId + "," + editTextComment.getText().toString());
                params.put("item_defect_id", itemDefectId);
                params.put("defect_type_id", selectedDefectTypeId);
                params.put("comment", editTextComment.getText().toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}