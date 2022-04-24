package com.indianecomliquidator.indianecomqc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    public static List<StoreDetailsInventory> listArrayProducts;
    private static RecyclerView mRecyclerProducts;
    private RecyclerView.LayoutManager layoutManager;
    static RequestQueue requestQueueProducts;
    private static AdapterProducts mAdapterProducts;
    int dpWidth, dpHeight;
    AlertDialog alertDialog, alertDialogPleaseWait;
    int count = 0;

    String productSubCategoryId, productSubCategoryName;
    LinearLayout linearLayoutNoResult;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sub_category_products);

        productSubCategoryId = getIntent().getStringExtra("product_sub_category_id");
        productSubCategoryName = getIntent().getStringExtra("product_sub_category_name");

        View view = getLayoutInflater().inflate(R.layout.custom_actionbar_product_categories, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        TextView Title = (TextView) view.findViewById(R.id.actionbar_title);
        Title.setText(productSubCategoryName);
        getSupportActionBar().setCustomView(view,params);
        getSupportActionBar().setDisplayShowCustomEnabled(true); //show custom title
        getSupportActionBar().setDisplayShowTitleEnabled(false); //hide the default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        dpWidth = size.x;
        dpHeight = size.y;

        linearLayoutNoResult = (LinearLayout) findViewById(R.id.linearLayoutNoResult);
        linearLayoutNoResult.setVisibility(View.INVISIBLE);

        listArrayProducts = new ArrayList<>();
        requestQueueProducts = Volley.newRequestQueue(ListSubCategoryProductsActivity.this);
        mRecyclerProducts = (RecyclerView) findViewById(R.id.ProductsRecyclerView);
        //layoutManager = new LinearLayoutManager(MobileAccessoriesActivity.this);

        layoutManager = new GridLayoutManager(ListSubCategoryProductsActivity.this, 2);
        mRecyclerProducts.setLayoutManager(layoutManager);

        mRecyclerProducts.setLayoutManager(layoutManager);
        mAdapterProducts = new AdapterProducts(listArrayProducts,ListSubCategoryProductsActivity.this, dpHeight, dpWidth, productSubCategoryId, productSubCategoryName);
        mRecyclerProducts.setAdapter(mAdapterProducts);

        getProductDetails();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_product_categories, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cart_button) {
            Intent intent = new Intent(ListSubCategoryProductsActivity.this, CartActivity.class);
            startActivity(intent);
        }else if(id == android.R.id.home){
            finish();
            onBackPressed();
        }else if (id == R.id.search_button) {
            Intent intent = new Intent(ListSubCategoryProductsActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getProductDetails() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = ListSubCategoryProductsActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        //To get host address got from firebase.
        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "phpfiles/getSubCategoryProducts118.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i("location", "Inside onResponse" + response);

                        try {
//                                alertDialogPleaseWait.cancel();
                        } catch (Exception e){}



//                            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarTaxi);
//                            final TextView textViewPleaseWait = (TextView) rootView.findViewById(R.id.taxiPleaseWait);

                        //Displaying Progressbar
//                            progressBar.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() != 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    StoreDetailsInventory storeDetailsInventory = new StoreDetailsInventory();
                                    JSONObject json = null;
                                    json = jsonArray.getJSONObject(i);

                                    //Adding data to the superhero object
                                    storeDetailsInventory.setProductId(json.getString("product_id"));
                                    storeDetailsInventory.setProductImage(json.getString("image_name"));
                                    storeDetailsInventory.setProductName(json.getString("product_name"));
                                    storeDetailsInventory.setPurchasePrice(json.getString("purchase_price"));
                                    storeDetailsInventory.setMrp(json.getString("mrp"));
                                    storeDetailsInventory.setSalePrice(json.getString("sale_price"));
                                    storeDetailsInventory.setProductUnitSymbol(json.getString("unit_symbol"));
                                    storeDetailsInventory.setProductUnitValue(json.getString("unit_value"));
                                    storeDetailsInventory.setShopName(json.getString("shop_name"));
                                    storeDetailsInventory.setInStock(json.getString("in_stock_full_count"));

                                    listArrayProducts.add(storeDetailsInventory);
                                }
                            }else{
                                linearLayoutNoResult.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try{
                            Bundle extras = ListSubCategoryProductsActivity.this.getIntent().getExtras();
                            if (extras.getString("recyclerviewPosition") != null) {
//                                Log.i("location", "inside scrollToPosition" + extras.getString("recyclerviewPosition"));
                                mRecyclerProducts.scrollToPosition(Integer.parseInt(extras.getString("recyclerviewPosition")));
                            }
                        }catch(Exception e){
                        }

                        //Notifying the adapter that data has been added or changed
                        mAdapterProducts.notifyDataSetChanged();
                        //Hiding the progressbar
//                            progressBar.setVisibility(View.GONE);
//                            textViewPleaseWait.setVisibility(View.INVISIBLE);

                        try {
                            alertDialogPleaseWait.cancel();
                        } catch (Exception e) {
                        }





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
                                getProductDetails();
                            } catch (Exception e) {

                            }
                        } else {

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ListSubCategoryProductsActivity.this);
                            LayoutInflater inflater = ListSubCategoryProductsActivity.this.getLayoutInflater();
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
                                    getProductDetails();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product_sub_category_id", productSubCategoryId);
                params.put("product_sub_category_name", productSubCategoryName);
                return params;
            }
        };
        requestQueueProducts.add(stringRequest);
    }
}
