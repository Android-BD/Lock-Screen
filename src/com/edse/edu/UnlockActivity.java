package com.edse.edu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockActivity extends Activity 
		
{
	// Directory where data file is stored in application directory

	

	private int countMoves = 0;
	private ImageView displayArrow = null;
	private TextView screenMessage = null;
	private String passwordSaved = null;
	private SharedPreferences settings;
	private static final String RECEIVED_PASSWORD = "unlock";
	private Editor myEditor;
	private String getBackupPin = null;
	private int numOfTries = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		
		
		displayArrow = (ImageView) findViewById(R.id.imageViewArrow);
		screenMessage = (TextView) findViewById(R.id.yt_axis);
		
		//get the password passed from the SetPasswordActivity if its already in internal
		//storage. It should look something like $12XJUY672SQ90JNML8912
		
		//Getting object's properties and assigning it to the passwordSaved variable.
		Intent intent = getIntent();
		passwordSaved = intent.getStringExtra(RECEIVED_PASSWORD);

		
		//getting shared preferences settings from prefs screen.
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
        getBackupPin = settings.getString("passBackupPref", null);
        
       
        
        
        //IF A CERTAIN NUMBER OF TRIES HAVE GONE BY THEN GIVE THE USER
        //THE OPTION TO ENTER THEIR PIN IF THEY ENTERED ON ON THE SETTINGS
        //MENU
        
        //PSUEDO CODE MORE OR LESS RIGHT NOW...
        if(numOfTries == 5 && getBackupPin != null)
        {
        	
			Dialog dialog;
			final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			EditText passTextBox = new EditText(this);
        	dialogBuilder.setView(passTextBox);
			dialogBuilder.setMessage("Are you sure you want to enter your pin?");
			dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					
					
				}
			});
			dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			dialog = dialogBuilder.create();
			dialog.show();
        }
		
	}

	


}