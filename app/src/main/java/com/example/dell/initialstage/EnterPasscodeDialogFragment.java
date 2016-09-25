package com.example.dell.initialstage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Dell on 24-Sep-16.
 */

public class EnterPasscodeDialogFragment extends DialogFragment {

    String event_name,passcode,user_entered_passcode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event_name=getArguments().getString("event_name");
        passcode=getArguments().getString("passcode");
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
                            //start activity
                            Intent i=new Intent(getActivity().getApplicationContext(),EventRound.class);
                            i.putExtra("event_name",event_name);
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
