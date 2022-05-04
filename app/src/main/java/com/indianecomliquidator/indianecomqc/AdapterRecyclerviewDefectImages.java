package com.indianecomliquidator.indianecomqc;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterRecyclerviewDefectImages extends RecyclerView.Adapter<AdapterRecyclerviewDefectImages.ViewHolder> {
    private Context context;
    int dpHeight;
    int dpWidth;

    //List to store all
    private List<StoreDetailsDefectImages> Details;
    private List<StoreDetailsDefectImages> DetailsFull;

    private int mPreviousPosition = 0;
    StoreDetailsDefectImages storeDetailsDefectImages;
    ClickListener listener;
    AlertDialog alertDialog, alertDialogPleaseWait;
    int count = 0;
    String productSubCategoryId, productSubCategoryName;

    //Constructor of this class
    AdapterRecyclerviewDefectImages(List<StoreDetailsDefectImages> Details, Context context, int dpHeight, int dpWidth){
        super();
        //Getting all
        this.Details = Details;
        this.context = context;
        this.dpHeight = dpHeight;
        this.dpWidth = dpWidth;
        this.productSubCategoryId = productSubCategoryId;
        this.productSubCategoryName = productSubCategoryName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_element, parent, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.imageViewProduct);
        imageView.getLayoutParams().width = dpWidth/3;
        imageView.getLayoutParams().height = dpWidth/3;

        final ViewHolder viewHolder = new ViewHolder(v, new ViewHolder.MyClickListener() {
            @Override
            public void onAddToCart(int p){
//                Button buttonAddToCart = (Button) v.findViewById(R.id.buttonAddToCart);
//                buttonAddToCart.setVisibility(View.INVISIBLE);
//                RelativeLayout relativeLayoutQuantity = (RelativeLayout) v.findViewById(R.id.relativeLayoutQuantity);
//                relativeLayoutQuantity.setVisibility(View.VISIBLE);
//                TextView textViewProductId = (TextView) v.findViewById(R.id.textViewProductId);
//                TextView textViewProductName = (TextView) v.findViewById(R.id.textViewProductName);
//                TextView textViewDefaultPurchasePrice = (TextView) v.findViewById(R.id.textViewDefaultPurchasePrice);
//                TextView textViewDefaultSalePrice = (TextView) v.findViewById(R.id.textViewDefaultSalePrice);
//                TextView textViewRequestCount = (TextView) v.findViewById(R.id.textViewRequestCount);
//                Integer productId = Integer.parseInt(textViewProductId.getText().toString());
//                String productName = textViewProductName.getText().toString();
//                String defaultPurchasePrice = textViewDefaultPurchasePrice.getText().toString();
//                String defaultSalePrice = textViewDefaultSalePrice.getText().toString();
//                String requestCount = textViewRequestCount.getText().toString();
//                Log.i("location", "Inside viewHolder onEdit" + productId + "Rs" + defaultPurchasePrice + "Rs" + defaultSalePrice);
//                SharedPreferences pref = context.getSharedPreferences("SelectedProductDetails", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putInt("selectedProductId", productId);
//                editor.putString("selectedProductName", productName);
//                editor.putString("selectedProductDefaultPurchasePrice", defaultPurchasePrice);
//                editor.putString("selectedProductDefaultSalePrice", defaultSalePrice);
//                editor.putInt("selectedProductRecyclerViewPosition", p);
//                editor.putString("requestCount", requestCount);
//                editor.commit();
////                ((Activity)context).finish();
//                Intent intent = new Intent(context, HomeActivity.class);
//                context.startActivity(intent);
            }

            @Override
            public void onDelete(final int p) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        TextView textViewProductId = (TextView) v.findViewById(R.id.textViewProductId);
                        String productId = textViewProductId.getText().toString();
                        toDeleteProduct(productId, context);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onClickRelativeLayoutProduct(final int p) {
                TextView textViewProductId = (TextView) v.findViewById(R.id.textViewProductId);
                TextView textViewProductName = (TextView) v.findViewById(R.id.textViewProductName);
                String productId = textViewProductId.getText().toString();
                Log.i("location", productId + "productId" + textViewProductName.getText().toString());
                SharedPreferences pref = context.getSharedPreferences("AppDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("qc_selected_product_id", productId);
                editor.putString("qc_selected_product_name", textViewProductName.getText().toString());
                editor.commit();
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onClickRelativeLayoutOutOfStock(int p) {

            }
        });

        DetailsFull = new ArrayList<>(Details);
        return viewHolder;
    }

    private void toDeleteProduct(final String productId, final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(true);

        SharedPreferences sharedPreferencesHostAddress = context.getSharedPreferences("HostAddress", MODE_PRIVATE);
        String hostAddress = sharedPreferencesHostAddress.getString("hostAddress", context.getString(R.string.host_address));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "phpFiles/deleteProduct.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.i("location", "Inside onResponse" + response);
                        try {
                                alertDialogPleaseWait.cancel();
                        } catch (Exception e){}

                        Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_LONG).show();
                        ((Activity)context).finish();
                        Intent i = new Intent(context, HomeActivity.class);
                        context.startActivity(i);
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
                                toDeleteProduct(productId, context);
                            } catch (Exception e) {

                            }
                        } else {
                                try {
                                    alertDialogPleaseWait.cancel();
                                } catch (Exception e) {
                                }

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            alertDialogBuilder.setView(inflater.inflate(R.layout.no_internet_dialog, null));
                            alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("productId", String.valueOf(productId));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //Getting the particular item from the list
        storeDetailsDefectImages =  Details.get(position);

        String hostAddress;
        SharedPreferences sharedPreferencesHostAddress = context.getSharedPreferences("HostAddress", MODE_PRIVATE);
        hostAddress = sharedPreferencesHostAddress.getString("hostAddress", context.getString(R.string.host_address));

        Uri uri = Uri.parse(hostAddress + "images/products/" + storeDetailsDefectImages.getProductImage());

        final Context context = holder.imageViewProduct.getContext();
        Picasso.with(context)
                .load(uri)
                .into(holder.imageViewProduct, new Callback() {
                    @Override
                    public void onSuccess() {
//                        holder.PBTaxiImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
//                        holder.PBTaxiImage.setVisibility(View.GONE);
                        holder.imageViewProduct.setImageResource(R.drawable.placeholder_image);
                    }
                });

//        holder.textViewProductId.setText(storeDetailsInventory.getProductId());
////        holder.textViewProductPage.setText(storeDetailsInventory.getProductId());
//        holder.textViewProductName.setText(storeDetailsInventory.getProductName());
//        holder.textViewDefaultPurchasePrice.setText(storeDetailsInventory.getDefaultPurchasePrice());
//        holder.textViewDefaultSalePrice.setText(storeDetailsInventory.getDefaultSalePrice());
//        if(!storeDetailsInventory.getSalePrice().equals(storeDetailsInventory.getMrp())) {
//            holder.textViewMrp.setText("\u20B9" + storeDetailsInventory.getMrp());
//            holder.textViewMrp.setPaintFlags(holder.textViewMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }
//        holder.textViewSalePrice.setText("\u20B9" + storeDetailsInventory.getSalePrice());
//        holder.textViewShopName.setText(storeDetailsInventory.getShopName());
//        holder.textViewStockUnit.setText(" " + storeDetailsInventory.getStockUnit());
//        holder.textViewProductUnitValue.setText(storeDetailsInventory.getProductUnitValue());
//        holder.textViewProductUnitSymbol.setText(" " + storeDetailsInventory.getProductUnitSymbol());
//        holder.textViewRequestCount.setText(storeDetailsInventory.getRequestCount());
//
//        if(storeDetailsInventory.getInStock().equals("0")) {
//            holder.relativeLayoutOutOfStock.setVisibility(View.VISIBLE);
//        }




//        ArrayAdapter<CharSequence> adapterPlan = ArrayAdapter.createFromResource(context,
//                R.array.unit_array, R.layout.unit_spinner_item);
//        adapterPlan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        holder.spinnerPlan.setAdapter(adapterPlan);
    }

    @Override
    public int getItemCount() {
        return Details == null ? 0 : Details.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        //Views
        public ImageView imageViewProduct;
        public TextView textViewProductId, textViewProductPage, textViewDefaultPurchasePrice, textViewDefaultSalePrice, textViewRequestCount;
        public TextView textViewProductName;
        public TextView textViewMrp, textViewSalePrice;
        public TextView textViewShopName;
        public TextView textViewStockUnit;
        public TextView textViewProductUnitValue, textViewProductUnitSymbol;
        public Button buttonAddToCart, buttonDeleteProduct;
        public RelativeLayout relativeLayoutProduct, relativeLayoutOutOfStock;

        MyClickListener listener;

//        Spinner spinnerPlan = (Spinner) itemView.findViewById(R.id.spinnerUnit);

        //Initializing Views
        public ViewHolder(View itemView, MyClickListener listener) {
            super(itemView);
            imageViewProduct = (ImageView) itemView.findViewById(R.id.imageViewProduct);
            textViewProductId = (TextView) itemView.findViewById(R.id.textViewProductId);
            textViewProductPage = (TextView) itemView.findViewById(R.id.textViewProductPage);
            textViewProductName = (TextView) itemView.findViewById(R.id.textViewProductName);
            textViewDefaultPurchasePrice = (TextView) itemView.findViewById(R.id.textViewDefaultPurchasePrice);
            textViewDefaultSalePrice = (TextView) itemView.findViewById(R.id.textViewDefaultSalePrice);
            textViewMrp = (TextView) itemView.findViewById(R.id.textViewMrp);
            textViewSalePrice = (TextView) itemView.findViewById(R.id.textViewSalePrice);
            textViewShopName = (TextView) itemView.findViewById(R.id.textViewShopName);
            textViewStockUnit = (TextView) itemView.findViewById(R.id.textViewStockUnit);
            textViewRequestCount = (TextView) itemView.findViewById(R.id.textViewRequestCount);
            textViewProductUnitValue = (TextView) itemView.findViewById(R.id.textViewProductUnitValue);
            textViewProductUnitSymbol= (TextView) itemView.findViewById(R.id.textViewProductUnitSymbol);
//            buttonAddToCart = (Button) itemView.findViewById(R.id.buttonAddToCart);
//            buttonDeleteProduct = (Button) itemView.findViewById(R.id.buttonDeleteProduct);
            relativeLayoutProduct = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutProduct);
            relativeLayoutOutOfStock = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutOutOfStock);

            this.listener = listener;
//            buttonAddToCart.setOnClickListener(this);
//            buttonDeleteProduct.setOnClickListener(this);
            relativeLayoutProduct.setOnClickListener(this);
            relativeLayoutOutOfStock.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.buttonAddToCart:
//                    listener.onAddToCart(this.getLayoutPosition());
//                    break;
//                case R.id.buttonDeleteProduct:
//                    listener.onDelete(this.getLayoutPosition());
//                    break;
                case R.id.relativeLayoutProduct:
                    listener.onClickRelativeLayoutProduct(this.getLayoutPosition());
                    break;
                case R.id.relativeLayoutOutOfStock:
                    listener.onClickRelativeLayoutOutOfStock(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }

        public interface MyClickListener {
            void onAddToCart(int p);
            void onDelete(int p);
            void onClickRelativeLayoutProduct(int p);
            void onClickRelativeLayoutOutOfStock(int p);
        }
    }


}
