package com.example.dell.initialstage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import android.widget.TextView;
import android.widget.Toast;

public class EventRound extends AppCompatActivity {


    String event_name,passcode, society, event_date, start_time, end_time;
    String question,question_no,answer,user_answer;
    int count_of_questions;
    Question[] q;
    public class Question{
        String question,question_no,answer,user_answer;

        public Question(String question_no,String question,String answer){
            this.question_no=question_no;
            this.question=question;
            this.answer=answer;
        }

        public void setUser_answer(String user_answer) {
            this.user_answer = user_answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getQuestion_no() {
            return question_no;
        }

        public String getAnswer() {
            return answer;
        }

        public String getUser_answer() {
            return user_answer;
        }
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_round);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        event_name=getIntent().getStringExtra("event_name");
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
                QuestionsDetails.FeedEntry.COLUMN_NAME_ANSWER
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

                q[i++]=new Question(question_no,question,answer);
            }while (c.moveToNext());
        }
        mSectionsPagerAdapter.notifyDataSetChanged();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.question_layout, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return count_of_questions;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position+"";
        }
    }
}
