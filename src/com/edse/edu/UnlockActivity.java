package com.edse.edu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UnlockActivity extends Activity 
		
{
	// Directory where data file is stored in application directory

	

	private int countMoves = 0;
	private ImageView displayArrow = null;
	private TextView screenMessage = null;
	private String passwordSaved = null;
	
	private static final String RECEIVED_PASSWORD = "unlock";

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


		
	}

	


}