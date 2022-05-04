package com.indianecomliquidator.indianecomqc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DrawTextOnImgActivity extends AppCompatActivity {

    ImageView ivDrawImg;
    String stringImage;
    String tempDefectImageFolder;

    AlertDialog alertDialogPleaseWait;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_text_on_img);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Mark Defect");

        ivDrawImg = (ImageView) findViewById(R.id.iv_draw);
//        Button btnSave = (Button) findViewById(R.id.btn_save);

//        btnSave.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                saveImg();
//
//            }
//        });


        SharedPreferences sharedPreferencesAppDetails = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
        Uri imageUri = Uri.parse(sharedPreferencesAppDetails.getString("selected_defect_image_uri", ""));
        tempDefectImageFolder = sharedPreferencesAppDetails.getString("temp_defect_image_folder", "");

        try {
            final InputStream imageStream;
            imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            ivDrawImg.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void saveImg(View view){
        Bitmap image = Bitmap.createBitmap(ivDrawImg.getWidth(), ivDrawImg.getHeight(), Bitmap.Config.RGB_565);
        ivDrawImg.draw(new Canvas(image));
        String uniqueString = UUID.randomUUID().toString();
//        String uri = Images.Media.insertImage(getContentResolver(), image, uniqueString, null);

        //encoding image to string
        stringImage = getStringImage(image);
        Log.i("location",stringImage + "stringImage");

        try {
            saveImage(image, uniqueString);
        } catch (IOException e) {
            e.printStackTrace();
        }
//
//        Log.i("location", uri);
//
//        try {
//            // Save the image to the SD card.
//
//            File folder = new File(Environment.getExternalStorageDirectory() + "/DrawTextOnImg");
//
//            if (!folder.exists()) {
//                folder.mkdir();
//                //folder.mkdirs();  //For creating multiple directories
//            }
//
//            File file = new File(Environment.getExternalStorageDirectory()+"/DrawTextOnImg/" + uniqueString + ".png");
//
//            FileOutputStream stream = new FileOutputStream(file);
//            image.compress(CompressFormat.PNG, 100, stream);
//            Toast.makeText(DrawTextOnImgActivity.this, "Picture saved", Toast.LENGTH_SHORT).show();
//
//            // Android equipment Gallery application will only at boot time scanning system folder
//            // The simulation of a media loading broadcast, for the preservation of images can be viewed in Gallery
//
//            /*Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
//            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
//            sendBroadcast(intent);*/
//
//        } catch (Exception e) {
//            Toast.makeText(DrawTextOnImgActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
//            Log.i("location", e + "Hi");
//            e.printStackTrace();
//        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public void sendImage(final View view) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = DrawTextOnImgActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "defects/storeimage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("location", "Inside onResponse" + response);
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
                                sendImage(view);
                            } catch (Exception e) {
                            }
                        } else {

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(DrawTextOnImgActivity.this);
                            LayoutInflater inflater = DrawTextOnImgActivity.this.getLayoutInflater();
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
                                    sendImage(view);
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
                Log.i("location", stringImage);
                params.put("image", stringImage);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = DrawTextOnImgActivity.this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "Indianecomqc/" + tempDefectImageFolder + "/");
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

}