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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.dell.initialstage.Register_user.url;

/**
 * Created by Dell on 24-Sep-16.
 */

public class EnterPasscodeDialogFragment extends DialogFragment {

    String event_name,passcode,user_entered_passcode,timer_value;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event_name=getArguments().getString("event_name");
        passcode=getArguments().getString("passcode");
        timer_value=getArguments().getString("timer_value");
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view=inflater.inflate(R.layout.popup_layout, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                               EditText ed=(EditText)view.findViewById(R.id.passcode_editText);
                        user_entered_passcode=ed.getText().toString();
                        if(passcode.equals(user_entered_passcode)){
                            Intent i = new Intent(getActivity(), EventRound.class);
                            i.putExtra("event_name", event_name);
                            i.putExtra("timer_value", timer_value);
                            startActivity(i);
                            getActivity().finish();
                        }else{
                            //Toast to
                            Toast.makeText(view.getContext(),"Haha you cannot play with this :P ",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EnterPasscodeDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
