package com.example.dell.initialstage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

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

        final TextView[] myTextViews = new TextView[rules_array.length]; // create an empty array;
        LinearLayout myLinearLayout= (LinearLayout) findViewById(R.id.rules_linear_layout);

        for (int i = 0; i < rules_array.length; i++) {

            // create a new textview
            final TextView ht = new TextView(this);
            // set some properties of rowTextView or something
            handler.postDelayed(new ViewUpdater(new String((i+1)+" ."+rules_array[i]),ht,myLinearLayout),1000+(i*1000));
            // add the textview to the linearlayout
            // save a reference to the textview for later
            myTextViews[i] = ht;

        }



        play_btn=(Button)findViewById(R.id.play_btn);

        final TextView timerValue;
        timerValue = (TextView) findViewById(R.id.timer_textView);
       // new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                timerValue.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                timerValue.setText("");
//                play_btn.setVisibility(View.VISIBLE);
//            }
//        }.start();

        play_btn.setVisibility(View.VISIBLE);
        timerValue.setVisibility(View.GONE);
        //This button will only be visible when event is online

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!passcode.equals("")){
                    Bundle b=new Bundle();
                    b.putString("event_name",event_name);
                    b.putString("passcode",passcode);
                    DialogFragment newDialogFragment=new EnterPasscodeDialogFragment();
                    newDialogFragment.setArguments(b);
                    newDialogFragment.show(getFragmentManager(),"passcode_fragment");

                }else{
                    Intent i=new Intent(getApplicationContext(),EventRound.class);
                    i.putExtra("event_name",event_name);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
    private class ViewUpdater implements Runnable{
        private String mString;
        private TextView mView;
        private LinearLayout linearLayout;

        public ViewUpdater(String string, TextView view,LinearLayout myLinearLayout){
            mString = string;
            mView = view;
            linearLayout=myLinearLayout;
        }

        @Override
        public void run() {
            mView.setTextSize(20);
            mView.setTextColor(getResources().getColor(R.color.colorAccent));
            mView.setText(mString);
            mView.setTypeface(Typeface.SANS_SERIF);
            linearLayout.addView(mView);
        }
    }


}
