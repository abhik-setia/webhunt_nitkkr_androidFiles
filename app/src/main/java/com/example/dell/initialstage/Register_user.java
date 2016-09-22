package com.example.dell.initialstage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        submit_btn=(Button)findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        event_name=getIntent().getStringExtra("event_name");
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //perform checks on form data





       //submit data and save it in device too

        //get response and get event info

    }

    public void registerUser(){
        // Instantiate the RequestQueue.
        progressDialog.setMessage("Giving you superpowers.. Please wait");
        progressDialog.show();


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url+"/register",null,
                new Response.Listener<JSONObject>() {
                    String error,error_message,register_status;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            error=response.getString("error");
                             error_message=response.getString("error_message");
                             register_status=response.getString("register_status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(error.equals("false")){
                            
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
