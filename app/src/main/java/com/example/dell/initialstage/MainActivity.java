package com.example.dell.initialstage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button webhunt_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webhunt_btn=(Button)findViewById(R.id.webhunt_event_btn);
        webhunt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent st=new Intent(getApplicationContext(),Register_user.class);
                st.putExtra("event_name","webhunt");
                startActivity(st);
            }
        });
    }
}
