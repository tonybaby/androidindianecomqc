package com.indianecomliquidator.indianecomqc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    public static List<StoreDetailsInventory> listArrayProducts, listArrayProductsFiltered;
    private static RecyclerView mRecyclerProducts;
    private RecyclerView.LayoutManager layoutManager;
    static RequestQueue requestQueueProducts;
    private static AdapterProducts mAdapterProducts;
    int dpWidth, dpHeight;
    AlertDialog alertDialog, alertDialogPleaseWait;
    int count = 0;

    String productSubCategoryId, productSubCategoryName;
    LinearLayout linearLayoutNoResult;
    Dialog dialog;
    ArrayList arrayListBrands;
    String itemGetDataResponse;
    String brandList, itemList;
    NachoTextView nachoTextViewBrands, nachoTextViewTags;
    ArrayList<String> selectedItemIds, selectedBrandIds;
    EditText editTextMinimumPrice, editTextMaximumPrice;
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
        setContentView(R.layout.activity_home);

        View view = getLayoutInflater().inflate(R.layout.custom_actionbar_product_categories, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        TextView Title = (TextView) view.findViewById(R.id.actionbar_title);
        Title.setText("Items");
        getSupportActionBar().setCustomView(view,params);
        getSupportActionBar().setDisplayShowCustomEnabled(true); //show custom title
        getSupportActionBar().setDisplayShowTitleEnabled(false); //hide the default title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        dpWidth = size.x;
        dpHeight = size.y;

        linearLayoutNoResult = (LinearLayout) findViewById(R.id.linearLayoutNoResult);
        linearLayoutNoResult.setVisibility(View.INVISIBLE);

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
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
        if(id == R.id.filter){
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
            final View viewDialog=inflater.inflate(R.layout.dialog_filter, null);
            alertDialogBuilder.setView(viewDialog);
            final androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCancelable(true);
            TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
            textViewMessageHead.setText("FILTER ITEMS");
            Button button1 = (Button) viewDialog.findViewById(R.id.button1);
            button1.setText("SUBMIT");
            editTextMinimumPrice = (EditText) viewDialog.findViewById(R.id.editTextMinPrice);
            editTextMaximumPrice = (EditText) viewDialog.findViewById(R.id.editTextMaxPrice);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    try {
                        alertDialog.cancel();
                    } catch (Exception e){}

                    try {
                        selectedBrandIds = new ArrayList<String>();
                        JSONArray jsonArray=new JSONArray(brandList);
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = null;
                                json = jsonArray.getJSONObject(i);
                                for (int j = 0; j < nachoTextViewBrands.getAllChips().size(); j++) {
                                    if(json.getString("name").equals(String.valueOf(nachoTextViewBrands.getAllChips().get(j)))){
                                        try {
                                            selectedBrandIds.add(json.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        Log.i("location", "selectedBrandIds" + selectedBrandIds);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        selectedItemIds = new ArrayList<String>();
                        JSONArray jsonArray=new JSONArray(itemList);
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = null;
                                json = jsonArray.getJSONObject(i);
                                for (int j = 0; j < nachoTextViewTags.getAllChips().size(); j++) {
                                    if(json.getString("name").equals(String.valueOf(nachoTextViewTags.getAllChips().get(j)))){
                                        try {
                                            selectedItemIds.add(json.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        Log.i("location", "selectedItemIds" + selectedItemIds);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    getProductDetailsFiltered();
                }
            });
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);

            brandList = sharedPreferences.getString("brand_list", "");
            arrayListBrands=new ArrayList<>();
            try {
                JSONArray jsonArray = null;
                jsonArray = new JSONArray(brandList);
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = null;
                        json = jsonArray.getJSONObject(i);
                        arrayListBrands.add(json.getString("name"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapterBrands = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, arrayListBrands);
            nachoTextViewBrands = (NachoTextView) viewDialog.findViewById(R.id.nachoTextViewBrands);
            nachoTextViewBrands.setAdapter(adapterBrands);

            ArrayList arrayListTags = new ArrayList<>();
            itemList = sharedPreferences.getString("item_list", "");
            try {
                JSONArray jsonArray = null;
                jsonArray = new JSONArray(itemList);
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = null;
                        json = jsonArray.getJSONObject(i);
                        arrayListTags.add(json.getString("name"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            String metaTagList = sharedPreferences.getString("meta_tag_list", "");
//            try {
//                JSONArray jsonArray = null;
//                jsonArray = new JSONArray(metaTagList);
//                if (jsonArray.length() != 0) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject json = null;
//                        json = jsonArray.getJSONObject(i);
//                        arrayListTags.add(json.getString("name"));
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            ArrayAdapter<String> adapterTags = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, arrayListTags);
            nachoTextViewTags = (NachoTextView) viewDialog.findViewById(R.id.nachoTextViewTags);
            nachoTextViewTags.setAdapter(adapterTags);

//            nachoTextView.setOnChipClickListener(new NachoTextView.OnChipClickListener() {
//                @Override
//                public void onChipClick(Chip chip, MotionEvent motionEvent) {
//
//                    Log.i("location", nachoTextView.getAllChips() + "hi");
//                    // Iterate over all of the chips in the NachoTextView
//                    for (Chip chipitem : nachoTextView.getAllChips()) {
//                        // Do something with the text of each chip
//                        CharSequence text = chipitem.getText();
//                        // Do something with the data of each chip (this data will be set if the chip was created by tapping a suggestion)
//                        Object data = chipitem.getData();
//                        Log.i("location", text + "," + data);
//                    }
//                }
//            });

//            textViewBrand.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Initialize dialog
//                    dialog=new Dialog(HomeActivity.this);
//
//                    // set custom dialog
//                    dialog.setContentView(R.layout.dialog_searchable_spinner);
//
//                    // set custom height and width
//                    dialog.getWindow().setLayout(650,800);
//
//                    // set transparent background
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                    // show dialog
//                    dialog.show();
//
//                    // Initialize and assign variable
//                    EditText editText=dialog.findViewById(R.id.edit_text);
//                    ListView listView=dialog.findViewById(R.id.list_view);
//
//                    // Initialize array adapter
//                    ArrayAdapter<String> adapter=new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1,arrayListBrands);
//
//                    // set adapter
//                    listView.setAdapter(adapter);
//                    editText.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                            adapter.getFilter().filter(s);
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//
//                        }
//
//                    });
//
//                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            // when item selected from list
//                            // set selected item on textView
//                            textViewBrand.setText(adapter.getItem(position));
//
//                            try {
//                                JSONArray jsonArray = new JSONArray(brandList);
//                                if (jsonArray.length() != 0) {
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        JSONObject json = null;
//                                        json = jsonArray.getJSONObject(i);
//                                        if(json.getString("name").equals(adapter.getItem(position))){
//                                                selectedBrandId = json.getString("id");
//                                                Log.i("location", selectedBrandId + "Hai");
//                                        }
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            // Dismiss dialog
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            });

        }else if (id == R.id.search_button) {
//            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
//            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getProductDetails() {

        listArrayProducts = new ArrayList<>();
        requestQueueProducts = Volley.newRequestQueue(HomeActivity.this);
        mRecyclerProducts = (RecyclerView) findViewById(R.id.ProductsRecyclerView);
        layoutManager = new GridLayoutManager(HomeActivity.this, 2);
        mRecyclerProducts.setLayoutManager(layoutManager);
        mAdapterProducts = new AdapterProducts(listArrayProducts,HomeActivity.this, dpHeight, dpWidth, productSubCategoryId, productSubCategoryName);
        mRecyclerProducts.setAdapter(mAdapterProducts);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        //To get host address got from firebase.
        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, hostAddress + "items/getdata",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        itemGetDataResponse = response;
                        Log.i("location", "Inside onResponse" + response);

                        try {
//                                alertDialogPleaseWait.cancel();
                        } catch (Exception e){}



//                            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarTaxi);
//                            final TextView textViewPleaseWait = (TextView) rootView.findViewById(R.id.taxiPleaseWait);

//                        Displaying Progressbar
//                            progressBar.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            SharedPreferences pref = getApplicationContext().getSharedPreferences("AppDetails", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("defect_type_list", jsonResponse.getString("defect_type_list"));
                            editor.commit();

                            Log.i("location", jsonResponse.getString("defect_type_list"));

                            JSONArray jsonArray = new JSONArray(jsonResponse.getString("item_list"));
                            if (jsonArray.length() != 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    StoreDetailsInventory storeDetailsInventory = new StoreDetailsInventory();
                                    JSONObject json = null;
                                    json = jsonArray.getJSONObject(i);

                                    //Adding data to the superhero object
                                    storeDetailsInventory.setProductId(json.getString("id"));
//                                    storeDetailsInventory.setProductImage(json.getString("image_name"));
                                    storeDetailsInventory.setProductName(json.getString("name"));
                                    Log.i("location home", "test" + json.getString("name") + "," + json.getString("id"));
//                                    storeDetailsInventory.setPurchasePrice(json.getString("purchase_price"));
//                                    storeDetailsInventory.setMrp(json.getString("mrp"));
//                                    storeDetailsInventory.setSalePrice(json.getString("sale_price"));
//                                    storeDetailsInventory.setProductUnitSymbol(json.getString("unit_symbol"));
//                                    storeDetailsInventory.setProductUnitValue(json.getString("unit_value"));
//                                    storeDetailsInventory.setShopName(json.getString("shop_name"));
//                                    storeDetailsInventory.setInStock(json.getString("in_stock_full_count"));

                                    listArrayProducts.add(storeDetailsInventory);
                                }
                            }else{
                                linearLayoutNoResult.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try{
                            Bundle extras = HomeActivity.this.getIntent().getExtras();
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

                        Log.i("location", "Inside onErrorResponse" + error);

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

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
                            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
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
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences sharedPreferencesUserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                final String token = sharedPreferencesUserDetails.getString("token", "");
                Log.i("location home", token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        requestQueueProducts.add(stringRequest);
    }

    private void getProductDetailsFiltered() {

        listArrayProducts = new ArrayList<>();
        requestQueueProducts = Volley.newRequestQueue(HomeActivity.this);
        mRecyclerProducts = (RecyclerView) findViewById(R.id.ProductsRecyclerView);

        layoutManager = new GridLayoutManager(HomeActivity.this, 2);
        mRecyclerProducts.setLayoutManager(layoutManager);

        mRecyclerProducts.setLayoutManager(layoutManager);
        mAdapterProducts = new AdapterProducts(listArrayProducts,HomeActivity.this, dpHeight, dpWidth, productSubCategoryId, productSubCategoryName);
        mRecyclerProducts.setAdapter(mAdapterProducts);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        //To get host address got from firebase.
        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "items/getdatafiltered",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        itemGetDataResponse = response;
                        Log.i("location", "Inside onResponse" + response);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(jsonResponse.getString("item_list"));
                            if (jsonArray.length() != 0) {
                                linearLayoutNoResult.setVisibility(View.INVISIBLE);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    StoreDetailsInventory storeDetailsInventory = new StoreDetailsInventory();
                                    JSONObject json = null;
                                    json = jsonArray.getJSONObject(i);
                                    storeDetailsInventory.setProductName(json.getString("name"));
//                                    Log.i("location home", "test" + json.getString("name"));
                                    listArrayProducts.add(storeDetailsInventory);
                                }
                            }else{
                                linearLayoutNoResult.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try{
                            Bundle extras = HomeActivity.this.getIntent().getExtras();
                            if (extras.getString("recyclerviewPosition") != null) {
//                                Log.i("location", "inside scrollToPosition" + extras.getString("recyclerviewPosition"));
                                mRecyclerProducts.scrollToPosition(Integer.parseInt(extras.getString("recyclerviewPosition")));
                            }
                        }catch(Exception e){
                        }

                        //Notifying the adapter that data has been added or changed
                        mAdapterProducts.notifyDataSetChanged();

                        try {
                            alertDialogPleaseWait.cancel();
                        } catch (Exception e) {
                        }
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
                                getProductDetailsFiltered();
                            } catch (Exception e) {

                            }
                        } else {

                            try {
                                alertDialogPleaseWait.cancel();
                            } catch (Exception e) {
                            }

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
                            LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
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
                                    getProductDetailsFiltered();
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
//                Log.i("location home", token);

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.i("locationmap", String.valueOf(selectedBrandIds) + "," + String.valueOf(selectedItemIds) + "," + String.valueOf(editTextMinimumPrice.getText()) + "," + String.valueOf(editTextMaximumPrice.getText()));
                params.put("selected_brand_ids", String.valueOf(selectedBrandIds));
                params.put("selected_item_ids", String.valueOf(selectedItemIds));
                params.put("minimum_price", String.valueOf(editTextMinimumPrice.getText()));
                params.put("maximum_price", String.valueOf(editTextMaximumPrice.getText()));
                return params;
            }
        };
        requestQueueProducts.add(stringRequest);
    }
}
