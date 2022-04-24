package com.indianecomliquidator.indianecomqc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewAppLogo;
    EditText editTextFullName, editTextPhone;
    Spinner spinnerCity;
    AlertDialog alertDialogPleaseWait, alertDialog;
    int count = 0;
    RequestQueue requestQueue;
    String hostAddress;
    SignInButton buttonGoogleSignIn;
    String email, city;

    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private ArrayList<String> arrayListCityId = new ArrayList<>();
    TextView textViewServiceArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

//        getSupportActionBar().hide();

        SharedPreferences sharedPreferencesHostAddress = getApplicationContext().getSharedPreferences("HostAddress", MODE_PRIVATE);
        hostAddress = sharedPreferencesHostAddress.getString("hostAddress", getString(R.string.host_address));

        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        int dpWidth = size.x;
        int dpHeight = size.y;

        imageViewAppLogo = (ImageView) findViewById(R.id.imageViewAppLogo);
        imageViewAppLogo.getLayoutParams().height = (int) (dpWidth/2);
        imageViewAppLogo.getLayoutParams().width = (int) (dpWidth/2);

        mAuth = FirebaseAuth.getInstance();
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn);
        buttonGoogleSignIn.setSize(SignInButton.SIZE_WIDE);
        setGooglePlusButtonText(buttonGoogleSignIn, "SIGN IN");
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.buttonGoogleSignIn).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        updateUI(account);
    }

    private void toHomeActivity() {
        finish();
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);

//        Toast.makeText(this, "Success!",
//                Toast.LENGTH_LONG).show();
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = SignInActivity.this.getLayoutInflater();
//        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
//        alertDialogPleaseWait = alertDialogBuilder.create();
//        alertDialogPleaseWait.show();
//        alertDialogPleaseWait.setCancelable(false);

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "register",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Log.i("location", "Inside onResponse" + response);
//
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            JSONObject jsonUser = new JSONObject(json.getString("user"));
//
//                            SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = pref.edit();
//                            editor.putString("jwt_token", json.getString("token"));
//                            editor.putString("user_id", jsonUser.getString("id"));
//                            editor.putString("user_type_id", jsonUser.getString("user_type_id"));
//                            editor.putString("user_name", jsonUser.getString("name"));
//                            editor.putString("user_phone", jsonUser.getString("phone"));
//                            editor.commit();
//
//                            finish();
//                            Intent i = new Intent(getApplicationContext(), ProductCategoriesActivity.class);
//                            startActivity(i);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
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
//                                registerUser();
//                            } catch (Exception e) {
//                            }
//                        } else {
//
//                            try {
//                                alertDialogPleaseWait.cancel();
//                            } catch (Exception e) {
//                            }
//
//                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(SignInOTPActivity.this);
//                            LayoutInflater inflater = SignInOTPActivity.this.getLayoutInflater();
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
//                                    registerUser();
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
////            @Override
////            public Map<String, String> getHeaders() throws AuthFailureError {
////                HashMap<String,String> headers = new HashMap<>();
////                headers.put("Content-Type","application/x-www-form-urlencoded");
////                return headers;
////            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", name);
//                params.put("phone", phone);
//                Log.i("location", FirebaseAuth.getInstance().getCurrentUser().getUid() + ", " + name + ", " + phone);
//                params.put("password", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                return params;
//            }
//        };
//        requestQueue.add(stringRequest);

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
//        alertDialogPleaseWait = alertDialogBuilder.create();
//        alertDialogPleaseWait.show();
//        alertDialogPleaseWait.setCancelable(false);
//
//        final String androidId = Settings.Secure.getString(CustomerSignUpActivity.this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, hostAddress + "phpfiles/signInCustomer.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            alertDialogPleaseWait.cancel();
//                        } catch (Exception e){}
//
////                        Log.i("location", response + "Hai");
//
//                        try {
//                            JSONArray jsonArray = new JSONArray(String.valueOf(response));
//                            if (jsonArray.length() != 0) {
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject json = null;
//                                    json = jsonArray.getJSONObject(i);
//
//                                    if(json.getString("array_type").equals("user_details")) {
//
//                                        SharedPreferences pref = getApplicationContext().getSharedPreferences("CustomerDetails", MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = pref.edit();
//                                        editor.putString("id", json.getString("user_id"));
//                                        editor.putString("name", String.valueOf(editTextFullName.getText()));
//                                        editor.putString("phone", ccp.getSelectedCountryCodeWithPlus() + String.valueOf(editTextPhone.getText()));
//                                        editor.putString("email", email);
//                                        editor.putString("cityId", arrayListCityId.get(spinnerCity.getSelectedItemPosition()-1));
//                                        editor.putString("city", city);
//                                        editor.putBoolean("signedIn", true);
//
//                                        editor.putString("additionalPhone", json.getString("additional_phone"));
//                                        editor.putString("houseName", json.getString("house_name"));
//                                        editor.putString("address", json.getString("address"));
//                                        editor.putString("landmark", json.getString("landmark"));
//                                        editor.putString("deliveryLocationLatitude", json.getString("delivery_location_latitude"));
//                                        editor.putString("deliveryLocationLongitude", json.getString("delivery_location_longitude"));
//                                        editor.putString("deliveryLocationName", json.getString("delivery_location_name"));
//                                        editor.putString("deliveryLocationDistance", json.getString("delivery_location_distance"));
//                                        //Log.i("location", "deliveryLocationDistance" + json.getString("delivery_location_distance"));
//
//                                        editor.putString("serviceArea", serviceArea);
//
//                                        editor.commit();
//
//                                        hideKeyboard(CustomerSignUpActivity.this);
//                                        finish();
//                                        Intent intent = new Intent(CustomerSignUpActivity.this, HomeActivity.class);
//                                        startActivity(intent);
//
//                                    }
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        try {
//                            if (response.trim().equals("error")) {
//                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerSignUpActivity.this);
//                                LayoutInflater inflater = CustomerSignUpActivity.this.getLayoutInflater();
//                                final View viewDialog=inflater.inflate(R.layout.dialog_normal, null);
//                                alertDialogBuilder.setView(viewDialog);
//                                final AlertDialog alertDialog = alertDialogBuilder.create();
//                                alertDialog.show();
//                                TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
//                                textViewMessageHead.setText("MESSAGE");
//                                TextView textViewMessage=(TextView)viewDialog.findViewById(R.id.textViewMessage);
//                                textViewMessage.setText("Something went wrong. Please try again");
//                                Button buttonReload = (Button) viewDialog.findViewById(R.id.button1);
//                                buttonReload.setText("RELOAD");
//                                buttonReload.setOnClickListener(new View.OnClickListener() {
//                                    public void onClick(View v)
//                                    {
//                                        try {
//                                            alertDialog.cancel();
//                                        } catch (Exception e){}
//                                        toHomeActivity();
//                                    }
//                                });
//                            } else if (isInteger(response.trim())){
//                            }
//                        } catch (Exception e){
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        try {
//                            alertDialogPleaseWait.cancel();
//                        } catch (Exception e) {
//                        }
//                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerSignUpActivity.this);
//                        LayoutInflater inflater = CustomerSignUpActivity.this.getLayoutInflater();
//                        final View viewDialog=inflater.inflate(R.layout.dialog_normal, null);
//                        alertDialogBuilder.setView(viewDialog);
//                        final AlertDialog alertDialog = alertDialogBuilder.create();
//                        alertDialog.show();
//                        TextView textViewMessageHead=(TextView)viewDialog.findViewById(R.id.textViewMessageHead);
//                        textViewMessageHead.setText("MESSAGE");
//                        TextView textViewMessage=(TextView)viewDialog.findViewById(R.id.textViewMessage);
//                        textViewMessage.setText("Something went wrong. Please check your internet connection.");
//                        Button buttonReload = (Button) viewDialog.findViewById(R.id.button1);
//                        buttonReload.setText("RELOAD");
//                        buttonReload.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v)
//                            {
//                                try {
//                                    alertDialog.cancel();
//                                } catch (Exception e){}
//                                toHomeActivity();
//                            }
//                        });
//                    }
//                }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("name", String.valueOf(editTextFullName.getText()));
//                params.put("email", email);
//                params.put("phone", ccp.getSelectedCountryCodeWithPlus() + String.valueOf(editTextPhone.getText()));
//                String spinnerCityString =String.valueOf(spinnerCity.getSelectedItem());
//                city = spinnerCityString.substring( 0, spinnerCityString.indexOf(" ("));
//                params.put("city", city);
//                params.put("deviceId", androidId);
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGoogleSignIn:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Log.i("location", "Inside signIn");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertDialogBuilder.setView(inflater.inflate(R.layout.please_wait_dialog, null));
        alertDialogPleaseWait = alertDialogBuilder.create();
        alertDialogPleaseWait.show();
        alertDialogPleaseWait.setCancelable(false);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            alertDialogPleaseWait.cancel();
        } catch (Exception e){}

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.i("location", "Inside onActivityResult");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

//            SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
//            String idToken = googleCredential.getGoogleIdToken();
//            if (idToken !=  null) {
//                // Got an ID token from Google. Use it to authenticate
//                // with Firebase.
//                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
//                mAuth.signInWithCredential(firebaseCredential)
//                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
////                                    Log.d(TAG, "signInWithCredential:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
////                                    updateUI(user);
//                                } else {
//                                    // If sign in fails, display a message to the user.
////                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
////                                    updateUI(null);
//                                }
//                            }
//                        });
//            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.i("location", "Inside handleSignInResult");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            email = account.getEmail();
            Log.i("location", account.getEmail() + "Haii");
            toHomeActivity();
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("location", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(16);
                tv.setTypeface(null, Typeface.BOLD);
                tv.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                tv.setText(buttonText);
                tv.setTextColor(Color.parseColor("#5BC883"));
                return;
            }
        }
    }

}
