package com.example.dell.initialstage;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register_user extends AppCompatActivity {

    private EditText user_name,user_roll_no,user_branch,user_year,user_email,user_phone_no;
    private String event_name;
    private Button submit_btn;
    private ProgressDialog progressDialog;
    private String url="http://192.168.1.5:3000";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        user_name=(EditText)findViewById(R.id.user_name);
        user_roll_no=(EditText)findViewById(R.id.user_roll_no);
        user_branch=(EditText)findViewById(R.id.user_branch);
        user_year=(EditText)findViewById(R.id.user_year);
        user_email=(EditText)findViewById(R.id.user_email);
        user_phone_no=(EditText)findViewById(R.id.user_phone_no);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        event_name=getIntent().getStringExtra("event_name");
        sharedPreferences=getSharedPreferences("register_status"+event_name, Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("user_name","").equals("")){
            fetch_event_details(0);
        }
        submit_btn=(Button)findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***perform checks on form data

                 **/

                registerUser();
            }
        });

    }
    public void fetch_event_details(final int status) {

        //status 0 means data is not stored in tables
        //status 1 means data is present in tables

        progressDialog.setMessage("Ahaa, We are locating the coordinates for your test");
        progressDialog.show();



            StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/events/" + event_name,
                    new Response.Listener<String>() {
                        String error, error_message, passcode, society, event_date, start_time, end_time;
                        String[] rules;
                        JSONObject[] questions;

                        @Override
                        public void onResponse(String result) {
                            try {
                                Log.v("hello", result);
                                JSONObject response = new JSONObject(result);
                                if (response.isNull("error")) {

                                    //initialise start and end time here !imp

                                    if (!response.isNull("passcode"))
                                        passcode = response.getString("passcode");
                                    society = response.getString("society");
                                    event_date = response.getString("event_date");
                                    JSONArray question_array = response.getJSONArray("questions");
                                    JSONArray rules_array = response.getJSONArray("rules");
                                        rules=new String[rules_array.length()];
                                for(int i=0;i<rules_array.length();i++){
                                    rules[i]=rules_array.getString(i);
                                }
                                    questions=new JSONObject[question_array.length()];
                                for(int i=0;i<question_array.length();i++){
                                    questions[i]=question_array.getJSONObject(i);
                                }



                                    //store it in sql table
                                    EVENT_DETAILS ev = new EVENT_DETAILS(getBaseContext());
                                    SQLiteDatabase db = ev.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    values.put(EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_NAME, event_name);
                                    values.put(EVENT_DETAILS.FeedEntry.COLUMN_NAME_PASSCODE, passcode);
                                    values.put(EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_DATE, event_date);
                                    //values.put(EVENT_DETAILS.FeedEntry.COLUMN_NAME_START_TIME,start_time);
                                    //values.put(EVENT_DETAILS.FeedEntry.COLUMN_NAME_END_TIME,end_time);

                                    db.insert(EVENT_DETAILS.FeedEntry.TABLE_NAME, null, values);

                                    //store questions and rules in different sql tables
                                    //pending here

                                    //start activity

                                    Intent st = new Intent(getApplicationContext(), Rules_timer.class);
                                    st.putExtra("event_name", event_name);
                                    startActivity(st);
                                    finish();

                                } else {
                                    error = response.getString("error");
                                    error_message = response.getString("error_message");
                                    if (error.equals("false")) {

                                    } else {
                                        Toast.makeText(getBaseContext(), error_message, Toast.LENGTH_LONG).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            progressDialog.cancel();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

    }



    public void registerUser(){
        // Instantiate the RequestQueue.

        progressDialog.setMessage("Giving you superpowers.. Please wait");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/register",
                new Response.Listener<String>() {
                    String error,error_message,register_status;

                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject response=new JSONObject(result);
                            error=response.getString("error");
                             error_message=response.getString("error_message");
                             register_status=response.getString("register_status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(error.equals("false")){

                        sharedPreferences=getSharedPreferences("register_status"+event_name, Context.MODE_PRIVATE);
                        editor=sharedPreferences.edit();

                            editor.putString("user_name",user_name.getText().toString());
                            editor.putString("user_roll_no",user_roll_no.getText().toString());
                            editor.putString("user_branch",user_branch.getText().toString());
                            editor.putString("user_year",user_year.getText().toString());
                            editor.putString("user_email",user_email.getText().toString());
                            editor.putString("user_phone_no",user_phone_no.getText().toString());
                            editor.apply();

                            //Now make a request for event details
                            fetch_event_details(0);

                        }else{
                              Toast.makeText(getBaseContext(),"Something went wrong. We will be right back",Toast.LENGTH_LONG).show();

                        }
                      //  Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();

                        progressDialog.cancel();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),error.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("event_name",event_name);
                params.put("user_name",user_name.getText().toString());
                params.put("user_roll_no",user_roll_no.getText().toString());
                params.put("user_branch",user_branch.getText().toString());
                params.put("user_year",user_year.getText().toString());
                params.put("user_email",user_email.getText().toString());
                params.put("user_phone_no",user_phone_no.getText().toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
