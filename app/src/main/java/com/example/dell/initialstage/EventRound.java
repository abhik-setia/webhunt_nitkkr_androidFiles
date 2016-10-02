package com.example.dell.initialstage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.dialogIcon;
import static android.R.attr.duration;
import static com.example.dell.initialstage.Register_user.url;

public class EventRound extends AppCompatActivity  {


    static String event_name;
    String passcode;
    String society;
    String event_date;
    String start_time;
    String end_time,timer_value;
    String question,question_no,answer,user_answer;
    int count_of_questions;
    static Question[] q;
    ProgressDialog progressDialog;
    static Button reset,save;
    static TabLayout tabLayout;
    TextView timerText;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_round);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        event_name=getIntent().getStringExtra("event_name");
        getSupportActionBar().setTitle(event_name.toUpperCase());
        //fetch event ddetails
        EVENT_DETAILS ev=new EVENT_DETAILS(getBaseContext());
        SQLiteDatabase db=ev.getReadableDatabase();

        String[] projection={
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_DATE,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_START_TIME,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_END_TIME,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_PASSCODE,
                EVENT_DETAILS.FeedEntry.COLUMN_NAME_EVENT_NAME
        };

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
        db.close();

        //fetch no of questions with count query to dynamically initalise tabs
        QuestionsDetails qd=new QuestionsDetails(getBaseContext());
        db=qd.getReadableDatabase();

        String[] projection2={
                QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME,
                QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO,
                QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION,
                QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER,
                QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER
        };
        selection= QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME+"=?";
        String[] selectionArgs2={event_name};
        c = db.query(
                QuestionsDetails.FeedEntry.TABLE_NAME,                     // The table to query
                projection2,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs2,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        int count=0;
        if(c.moveToFirst()){
            do{
                count++;
            }while (c.moveToNext());
        }
        q=new Question[count];
        count_of_questions=count;
        int i=0;
        if(c.moveToFirst()){
            do{
                question=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION));
                question_no=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO));
                answer=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER));
                user_answer=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER));
                q[i]=new Question(question_no,question,answer);
                q[i++].setUser_answer(user_answer);
            }while (c.moveToNext());
        }

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

         tabLayout= (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        timer_value=getIntent().getStringExtra("timer_value");
    }

    public static class QuestionFragment extends Fragment implements View.OnClickListener{

        Typewriter question_textview;
        EditText answer_edit_text;
        private static final String DESCRIBABLE_KEY = "describable_key";

        public QuestionFragment(){

        }

        public static QuestionFragment newInstance(Question q){
            QuestionFragment qf=new QuestionFragment();
            Bundle bundle=new Bundle();
            bundle.putSerializable(DESCRIBABLE_KEY,q);
            qf.setArguments(bundle);
            return qf;
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        Question question_object;
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.question_layout, container, false);

            question_textview=(Typewriter)rootView.findViewById(R.id.question_textview);
            answer_edit_text=(EditText)rootView.findViewById(R.id.answer_edit_text);
            Bundle b=getArguments();
            question_object = (Question) b.getSerializable(DESCRIBABLE_KEY);
            question_textview.animateText(question_object.getQuestion_no()+". "+question_object.getQuestion());
            question_textview.setCharacterDelay(50);
            Animation fadeinanimation=AnimationUtils.loadAnimation(getContext(),R.anim.fade_in_animation);
            question_textview.startAnimation(fadeinanimation);
            answer_edit_text.startAnimation(fadeinanimation);

            if(question_object.getUser_answer()!=null )
            {
                answer_edit_text.setText(question_object.getUser_answer());
            }

            reset=(Button)rootView.findViewById(R.id.reset_btn);
            save=(Button)rootView.findViewById(R.id.save_button);

            save.setOnClickListener(this);
            reset.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()){
                case R.id.save_button:
                    if(answer_edit_text.getText().toString().trim().equals("")){
                        Animation shake= AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
                        answer_edit_text.startAnimation(shake);

                    }else {
                        QuestionsDetails ev=new QuestionsDetails(getActivity());
                        SQLiteDatabase db=ev.getWritableDatabase();
                        QuestionsDetails.UpdateAnswer(db, answer_edit_text.getText().toString(),question_object,event_name);
                        question_object.setUser_answer(answer_edit_text.getText().toString());
                        Toast.makeText(v.getContext(),"Saved succesfully :) ",Toast.LENGTH_LONG).show();
                        db.close();

                        //tabLayout.getTabAt(Integer.parseInt(question_object.getQuestion_no())-1);
                        //tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).setIcon(v.getResources().getDrawable(R.drawable.fire);
                    }

                    break;
                case R.id.reset_btn:
                    new AlertDialog.Builder(v.getContext())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Reset Answer")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    QuestionsDetails ev=new QuestionsDetails(getActivity());
                                    SQLiteDatabase db=ev.getWritableDatabase();
                                    answer_edit_text.setText("");
                                    QuestionsDetails.ResetAnswer(db,question_object,event_name);
                                    Toast.makeText(v.getContext(),"Reset Successfully",Toast.LENGTH_SHORT).show();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

            }

        }
    }

    private void setupViewPager(ViewPager viewPager){

        ViewPagerAdapter vw=new ViewPagerAdapter(getSupportFragmentManager());
        String question,question_no,answer,user_answer;

        for(int i=0;i<count_of_questions;i++) {
            vw.addFrag(QuestionFragment.newInstance(q[i]), i + 1 + "");
        }viewPager.setAdapter(vw);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Test")
                .setMessage("Are you sure you want to exit and submit test ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSubmit=0;
                        start_submitting_answers(0);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSubmit=0;
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_round, menu);
        MenuItem timerItem = menu.findItem(R.id.break_timer);
        timerText= (TextView) MenuItemCompat.getActionView(timerItem);
        timerText.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
        startTimer(Integer.valueOf(timer_value),1000);
        return true;
    }
    static int secondsLeft;
private void startTimer(long duration, long interval) {

    CountDownTimer timer = new CountDownTimer(duration, interval) {

        @Override
        public void onFinish() {
            start_submitting_answers(1);
        }

        @Override
        public void onTick(long millisecondsLeft) {
            secondsLeft = (int) Math.round((millisecondsLeft / (double) 1000));
            timerText.setText(secondsToString(secondsLeft));
        }
    };

    timer.start();
}

    @Override
    protected void onPause() {
        super.onPause();

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_finish) {
            start_submitting_answers(0);
        }

        return super.onOptionsItemSelected(item);
    }
    int x=0;
    public void start_submitting_answers(int status){
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Congrats you had won 2 million dollars,just kidding :P uploading your answers...");
        progressDialog.show();

        QuestionsDetails qd=new QuestionsDetails(getBaseContext());
        SQLiteDatabase db=qd.getReadableDatabase();

        String[] projection2={
                QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME,
                QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO,
                QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION,
                QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER,
                QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER
        };
        String selection= QuestionsDetails.FeedEntry.COLUMN_NAME_EVENT_NAME+"=?";
        String[] selectionArgs2={event_name};
        Cursor c = db.query(
                QuestionsDetails.FeedEntry.TABLE_NAME,                     // The table to query
                projection2,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs2,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if(c.moveToFirst()){
            do{
                question=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION));
                question_no=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_QUESTION_NO));
                answer=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER));
                user_answer=c.getString(c.getColumnIndexOrThrow(QuestionsDetails.FeedEntry.COLUMN_NAME_USER_ANSWER));
                if(user_answer!=null){
                    submit_answers(question_no,user_answer,answer,status);
                }else{
                    submit_answers(question_no,new String(""),answer,status);
                }
            }while (c.moveToNext());
        }
       }

    private static int isSubmit=0;

    public void submit_answers(final String answer_no, final String answer, final String original_answer, final int status){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/events/submitTest",
                new Response.Listener<String>() {
                    String error,error_message,register_status;

                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject response=new JSONObject(result);
                            error=response.getString("error");
                            error_message=response.getString("error_message");

                            if(error.equals("true")){
                                Toast.makeText(getBaseContext(),"Something went wrong on server side.",Toast.LENGTH_SHORT).show();
                                //ask_user_again();
                                progressDialog.cancel();

                            }else{
                                if(Integer.valueOf(answer_no)==count_of_questions){
                                    progressDialog.cancel();
                                    startActivity(new Intent(getApplicationContext(),ThankYou.class));
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.cancel();
                        //Toast.makeText(getBaseContext(),error.toString(), Toast.LENGTH_LONG).show();
                        isSubmit++;
                        if(isSubmit==1)
                        ask_user_again(status);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                SharedPreferences sharedPreferences=getSharedPreferences("register_status"+event_name, Context.MODE_PRIVATE);

                Map<String,String> params = new HashMap<String, String>();
                params.put("event_name",event_name);
                params.put("user_email",sharedPreferences.getString("user_email","").toString());
                params.put("answer_no",answer_no);
                params.put("answer",answer);
                params.put("original_answer",original_answer);
                return params;
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }
    public void ask_user_again(final int status){
        new AlertDialog.Builder(EventRound.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("We lost you buddy. Network failure,Submit again ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        isSubmit=0;
                        if(isNetworkAvailable())
                        start_submitting_answers(status);
                        else{
                            try {
                                setMobileDataEnabled(EventRound.this,false);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(EventRound.this,"Check your internet connection please",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSubmit=0;
                        if(status==1)
                         ask_user_again(1);
                    }
                })
                .show();
    }
}
