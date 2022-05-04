package com.indianecomliquidator.indianecomqc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    AlertDialog alertDialogPleaseWait, alertDialog, alertDialogProductStatus;
    int count = 0;
    Spinner spinnerProductStatus;
    private ArrayList<String> arrayListProductStatus = new ArrayList<>();
    String selectedProductStatus;
    String productId;
    TextView textViewProductName;
    String qCSelectedProductId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Product Details");

        SharedPreferences sharedPreferencesAppDetails = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
        qCSelectedProductId = sharedPreferencesAppDetails.getString("qc_selected_product_id", "");
        String qc_selected_product_name = sharedPreferencesAppDetails.getString("qc_selected_product_name", "");
        Log.i("location" ,qCSelectedProductId);

        textViewProductName = (TextView) findViewById(R.id.textViewProductName);
        textViewProductName.setText(qc_selected_product_name);



        arrayListProductStatus.add("Ready To Sale");
        arrayListProductStatus.add("Defective");
    }


//    @Override
//    public void onBackPressed() {
////        finish();
////        Intent intent = new Intent(ProductDetailsActivity.this, MobileAccessoriesActivity.class);
////        startActivity(intent);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void toShowProductStatusDialog(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ProductDetailsActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_product_status, null);
        alertDialogBuilder.setView(dialogView);
        alertDialogProductStatus = alertDialogBuilder.create();
        alertDialogProductStatus.show();
        TextView textViewMessageHead=(TextView)dialogView.findViewById(R.id.textViewMessageHead);
        textViewMessageHead.setText("SELECT PRODUCT STATUS");
        Button button1 = (Button) dialogView.findViewById(R.id.button1);
        button1.setText("SUBMIT");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(selectedProductStatus.equals("Defective")){
                    setItemDefectId();
                }
            }
        });

        spinnerProductStatus = (Spinner) dialogView.findViewById(R.id.spinnerProductStatus);

        ArrayAdapter<String> adapterProductStatus = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_spinner_item, arrayListProductStatus);
        adapterProductStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductStatus.setAdapter(adapterProductStatus);

        spinnerProductStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProductStatus = (String) spinnerProductStatus.getSelectedItem();
                Log.i("location", selectedProductStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setItemDefectId() {
        RequestQueue requestQueue = Volley.newRequestQueue(ProductDetailsActivity.this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ProductDetailsActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        //To get host address got from firebase.
        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "itemdefects/store",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("location", "Inside onResponse" + response);
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("item_defect_id", json.getString("item_defect_id"));
                            editor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            alertDialogPleaseWait.cancel();
                        } catch (Exception e) {
                        }

                        Log.i("location", selectedProductStatus + "selectedProductStatus");
                        alertDialogProductStatus.cancel();
                        Intent intent = new Intent(getApplicationContext(), DefectsActivity.class);
                        intent.putExtra("product_id", productId);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("location", "Inside onErrorResponse" + error);
                        if (count < 2) {
                            count++;
                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }
                            try {
                                setItemDefectId();
                            } catch (Exception e) {
                            }
                        } else {

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ProductDetailsActivity.this);
                            LayoutInflater inflater = ProductDetailsActivity.this.getLayoutInflater();
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
                                    setItemDefectId();
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
                params.put("item_id", String.valueOf(qCSelectedProductId));
                Log.i("location", qCSelectedProductId + "qCSelectedProductId");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
