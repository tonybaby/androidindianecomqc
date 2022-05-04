package com.indianecomliquidator.indianecomqc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hootsuite.nachos.NachoTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DefectsActivity extends AppCompatActivity {
    String productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defects);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Defects");

        getItemDefects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_defects, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.cart_button) {
//            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
//            startActivity(intent);
//        }else
        if(id == R.id.add){
            Intent intent = new Intent(DefectsActivity.this, AddDefectActivity.class);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getItemDefects() {

//        listArrayProducts = new ArrayList<>();
//        requestQueueProducts = Volley.newRequestQueue(HomeActivity.this);
//        mRecyclerProducts = (RecyclerView) findViewById(R.id.ProductsRecyclerView);
//
//        layoutManager = new GridLayoutManager(HomeActivity.this, 2);
//        mRecyclerProducts.setLayoutManager(layoutManager);
//
//        mRecyclerProducts.setLayoutManager(layoutManager);
//        mAdapterProducts = new AdapterProducts(listArrayProducts,HomeActivity.this, dpHeight, dpWidth, productSubCategoryId, productSubCategoryName);
//        mRecyclerProducts.setAdapter(mAdapterProducts);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
//        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
//        alertDialogPleaseWait = alertDialogBuilder.create();
//        alertDialogPleaseWait.show();
//        alertDialogPleaseWait.setCancelable(false);
//
//        //To get host address got from firebase.
//        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
//        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "items/getdatafiltered",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        itemGetDataResponse = response;
//                        Log.i("location", "Inside onResponse" + response);
//
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            JSONArray jsonArray = new JSONArray(jsonResponse.getString("item_list"));
//                            if (jsonArray.length() != 0) {
//                                linearLayoutNoResult.setVisibility(View.INVISIBLE);
//
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    StoreDetailsInventory storeDetailsInventory = new StoreDetailsInventory();
//                                    JSONObject json = null;
//                                    json = jsonArray.getJSONObject(i);
//                                    storeDetailsInventory.setProductName(json.getString("name"));
////                                    Log.i("location home", "test" + json.getString("name"));
//                                    listArrayProducts.add(storeDetailsInventory);
//                                }
//                            }else{
//                                linearLayoutNoResult.setVisibility(View.VISIBLE);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        try{
//                            Bundle extras = HomeActivity.this.getIntent().getExtras();
//                            if (extras.getString("recyclerviewPosition") != null) {
////                                Log.i("location", "inside scrollToPosition" + extras.getString("recyclerviewPosition"));
//                                mRecyclerProducts.scrollToPosition(Integer.parseInt(extras.getString("recyclerviewPosition")));
//                            }
//                        }catch(Exception e){
//                        }
//
//                        //Notifying the adapter that data has been added or changed
//                        mAdapterProducts.notifyDataSetChanged();
//
//                        try {
//                            alertDialogPleaseWait.cancel();
//                        } catch (Exception e) {
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Log.i("location", "Inside onErrorResponse" + error);
//
//                        if (count < 2) {
//                            count++;
//
//                            try {
//                                alertDialogPleaseWait.cancel();
//                            } catch (Exception e) {
//                            }
//                            try {
//                                getProductDetailsFiltered();
//                            } catch (Exception e) {
//
//                            }
//                        } else {
//
//                            try {
//                                alertDialogPleaseWait.cancel();
//                            } catch (Exception e) {
//                            }
//
//                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
//                            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
//                            final View viewDialog=inflater.inflate(R.layout.dialog_2_buttons, null);
//                            alertDialogBuilder.setView(viewDialog);
//                            final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
//                            alertDialog.show();
//                            alertDialog.setCancelable(false);
//                            TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
//                            textViewMessageHead.setText("MESSAGE");
//                            TextView textViewMessage=(TextView)viewDialog.findViewById(R.id.textViewMessage);
//                            textViewMessage.setText("Something went wrong. Please check your internet connection or try again later.");
//                            Button button1 = (Button) viewDialog.findViewById(R.id.button1);
//                            button1.setText("RELOAD");
//                            button1.setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v)
//                                {
//                                    try {
//                                        alertDialog.cancel();
//                                    } catch (Exception e){}
//                                    getProductDetailsFiltered();
//                                }
//                            });
//                            Button button2 = (Button) viewDialog.findViewById(R.id.button2);
//                            button2.setText("CLOSE");
//                            button2.setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v)
//                                {
//                                    try {
//                                        alertDialog.cancel();
//                                    } catch (Exception e){}
//                                }
//                            });
//                        }
//
//                    }
//                }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                SharedPreferences sharedPreferencesUserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
//                final String token = sharedPreferencesUserDetails.getString("token", "");
////                Log.i("location home", token);
//
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", "Bearer " + token);
//                return params;
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                Log.i("locationmap", String.valueOf(selectedBrandIds) + "," + String.valueOf(selectedItemIds) + "," + String.valueOf(editTextMinimumPrice.getText()) + "," + String.valueOf(editTextMaximumPrice.getText()));
//                params.put("selected_brand_ids", String.valueOf(selectedBrandIds));
//                params.put("selected_item_ids", String.valueOf(selectedItemIds));
//                params.put("minimum_price", String.valueOf(editTextMinimumPrice.getText()));
//                params.put("maximum_price", String.valueOf(editTextMaximumPrice.getText()));
//                return params;
//            }
//        };
//        requestQueueProducts.add(stringRequest);
    }
}