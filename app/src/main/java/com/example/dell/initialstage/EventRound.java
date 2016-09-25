package com.example.dell.initialstage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventRound extends AppCompatActivity  {


    static String event_name;
    String passcode;
    String society;
    String event_date;
    String start_time;
    String end_time;
    String question,question_no,answer,user_answer;
    int count_of_questions;
    static Question[] q;
    static Button reset,save;


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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public static class QuestionFragment extends Fragment implements View.OnClickListener{

        TextView question_textview;
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

            question_textview=(TextView)rootView.findViewById(R.id.question_textview);
            answer_edit_text=(EditText)rootView.findViewById(R.id.answer_edit_text);
            Bundle b=getArguments();
            question_object = (Question) b.getSerializable(DESCRIBABLE_KEY);
            question_textview.setText(question_object.getQuestion_no()+". "+question_object.getQuestion());
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
                        Toast.makeText(getBaseContext(),"Thank you for taking part in this test",Toast.LENGTH_LONG).show();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_round, menu);
        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_finish) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
