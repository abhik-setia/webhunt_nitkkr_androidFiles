package com.example.dell.initialstage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.dell.initialstage.Register_user.url;

public class Rules_timer extends AppCompatActivity {

    String event_name,passcode, society, event_date, start_time, end_time;
    String rules_json;
    String[] rules_array;
    private Handler handler=new Handler();
    private Button play_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EVENT_DETAILS ev=new EVENT_DETAILS(getBaseContext());
        SQLiteDatabase db=ev.getReadableDatabase();

        String[] projection={
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_DATE,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_START_TIME,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_END_TIME,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_PASSCODE,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_NAME
        };
        event_name=getIntent().getStringExtra("event_name");
        rules_json=getIntent().getStringExtra("rules");
        try {
            JSONArray jsonArray=new JSONArray(rules_json);
            rules_array=new String[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++){
                rules_array[i]=jsonArray.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        String selection= EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_NAME+"=?";
        String[] selectionArgs={event_name};

        Cursor c = db.query(
                EVENT_DETAILS.FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();

        event_date=c.getString(c.getColumnIndexOrThrow(EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_DATE));
        start_time=c.getString(c.getColumnIndexOrThrow(EVENT_DETAILS.FeedEntry.COLUMN_NAME_START_TIME));
        end_time=c.getString(c.getColumnIndexOrThrow(EVENT_DETAILS.FeedEntry.COLUMN_NAME_END_TIME));
        passcode=c.getString(c.getColumnIndexOrThrow(EVENT_DETAILS.FeedEntry.COLUMN_NAME_PASSCODE));
        final Typewriter[] myTextViews = new Typewriter[rules_array.length]; // create an empty array;
        LinearLayout myLinearLayout= (LinearLayout) findViewById(R.id.rules_linear_layout);

        for (int i = 0; i < rules_array.length; i++) {

            // create a new textview
            final Typewriter ht = new Typewriter(this);
            // set some properties of rowTextView or something
            handler.postDelayed(new ViewUpdater(new String((i+1)+" ."+rules_array[i]),ht,myLinearLayout),(i*2000));
            // add the textview to the linearlayout
            // save a reference to the textview for later
            myTextViews[i] = ht;

        }

        play_btn=(Button)findViewById(R.id.play_btn);

        final TextView timerValue= (TextView) findViewById(R.id.timer_textView);
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            Date date = format.parse(event_date);

            Date adjustedDate=new Date();
            adjustedDate.setTime(date.getTime()+19800000);

            final Calendar calendar=Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String formatted_date=df.format(calendar.getTime());
           // Log.v("hello",date.getTime()-calendar.getTimeInMillis()+"");

            if(formatted_date.charAt(0)== '0' ){
                int l=formatted_date.length();
                formatted_date=formatted_date.substring(1,l).trim();
            }

           // Log.v("hello",date.toGMTString().substring(0,11).trim()+" "+formatted_date);

            if((adjustedDate.toGMTString().substring(0,11).trim()+"").equals(formatted_date)){


                play_btn.setVisibility(View.GONE);
                timerValue.setVisibility(View.VISIBLE);
                Date date2 = format.parse(start_time);
                final Date end_date=format.parse(end_time);

                Log.v("hello",end_date.getTime()+" "+date2.getTime()+" "+calendar.getTimeInMillis());

                if((end_date.getTime()-calendar.getTimeInMillis())<0){
                    play_btn.setVisibility(View.GONE);
                    timerValue.setVisibility(View.VISIBLE);
                    timerValue.setText("Event has ended");
                }else {
                    //remember the difference between time zone is 5:30 hours
                    int adjusted_time = (int) (date2.getTime() - calendar.getTimeInMillis());

                    new CountDownTimer(adjusted_time, 1000) {

                        public void onTick(long millisUntilFinished) {
                            int seconds_left= (int) (millisUntilFinished / 1000);
                            timerValue.setText("Event will begin in: " + secondsToString(seconds_left));
                        }

                        public void onFinish() {

                            timerValue.setText("");
                            play_btn.setVisibility(View.VISIBLE);
                            int adjusted_time = (int) (end_date.getTime() - calendar.getTimeInMillis());

                        }
                    }.start();
                }
            }else{
                play_btn.setVisibility(View.GONE);
                timerValue.setVisibility(View.VISIBLE);
                timerValue.setText("Event will start on "+(adjustedDate.toGMTString().substring(0,11)+""));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //This button will only be visible when event is online

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!passcode.equals("")){
                    verify();
                }else{

                    //make a request and fetch timer value

                    //!imp  !imp !imp This step has been deprecated due to security

                    Intent i=new Intent(getApplicationContext(),EventRound.class);
                    i.putExtra("event_name",event_name);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
    public void verify(){
        final ProgressDialog progressDialog=new ProgressDialog(Rules_timer.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying... Please wait");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/events/isEventActive",
                new Response.Listener<String>() {
                    String error,error_message,register_status;

                    @Override
                    public void onResponse(String result) {

                            try {
                                JSONObject response = new JSONObject(result);
//                            error=response.getString("error");
//                            error_message=response.getString("error_message");
//
//                            if(error.equals("true")){
//                                Toast.makeText(getActivity(),"Something went wrong,Trying to submit again",Toast.LENGTH_SHORT).show();
//                                progressDialog.cancel();
//                            }else{

                               String timer_value = response.getString("timer_value");

                                if (!timer_value.equals("-1")) {
                                    //start activity
                                    progressDialog.cancel();
                                    Bundle b=new Bundle();
                                    b.putString("event_name",event_name);
                                    b.putString("passcode",passcode);
                                    b.putString("timer_value",timer_value);
                                    DialogFragment newDialogFragment=new EnterPasscodeDialogFragment();
                                    newDialogFragment.setArguments(b);
                                    newDialogFragment.show(getFragmentManager(),"passcode_fragment");
                                } else {
                                    Toast.makeText(getBaseContext(), "Your time slot has ended,Thanks", Toast.LENGTH_LONG).show();
                                    progressDialog.cancel();

                                }
//                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                SharedPreferences sharedPreferences=getSharedPreferences("register_status"+event_name, Context.MODE_PRIVATE);

                Map<String,String> params = new HashMap<String, String>();
                params.put("event_name",event_name);
                params.put("user_email",sharedPreferences.getString("user_email","").toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        requestQueue.add(stringRequest);


    }
    private String secondsToString(int improperSeconds) {

        //Seconds must be fewer than are in a day

        Time secConverter = new Time();

        secConverter.hour = 0;
        secConverter.minute = 0;
        secConverter.second = 0;

        secConverter.second = improperSeconds;
        secConverter.normalize(true);

        String hours = String.valueOf(secConverter.hour);
        String minutes = String.valueOf(secConverter.minute);
        String seconds = String.valueOf(secConverter.second);

        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (hours.length() < 2) {
            hours = "0" + hours;
        }

        String timeString = hours + ":" + minutes + ":" + seconds;
        return timeString;
    }
    private class ViewUpdater implements Runnable{
        private String mString;
        private Typewriter mView;
        private LinearLayout linearLayout;

        public ViewUpdater(String string, Typewriter view,LinearLayout myLinearLayout){
            mString = string;
            mView = view;
            linearLayout=myLinearLayout;
        }

        @Override
        public void run() {
            mView.setTextSize(20);
            mView.setTextColor(getResources().getColor(R.color.colorAccent));
            mView.setTypeface(Typeface.SANS_SERIF);
            mView.setCharacterDelay(50);
            mView.animateText(mString);
            linearLayout.addView(mView);

        }

    }

}
