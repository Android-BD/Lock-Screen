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

public class UnlockActivity extends Activity implements
		SensorEventListener
{
	// Directory where data file is stored in application directory

	
	private StringBuilder moveBuilder = new StringBuilder();
	private StringBuilder password = new StringBuilder();
	private float[] history = new float[2];
	private String[] direction = { "NONE" };
	private SensorManager manager = null;
	private Sensor accelerometer = null;
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


		try
		{

			//First thing to do in the application is to check the internal storage
			//on the device. So here I am calling the method in the Util class
			//to do that.
			Util.CheckInternalStorage(getApplicationContext());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword);


		// Just attaching listeners to the sensors....
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		manager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		
		

		
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{

		// changed directions
		float xChange = history[0] - event.values[0];
		float yChange = history[1] - event.values[1];

		history[0] = event.values[0];
		history[1] = event.values[1];

		// if (xChange < NOISE) xChange = (float)0.0;

		// if (yChange < NOISE) yChange = (float)0.0;
		// values are multiplied to work with large numbers such as 4 or 5
		// rather than
		// 0.348738473484 or -0.00838483.

		//Actually the multiplication here is partially just to make the numbers larger
		//and easier to deal with. Everything is getting multiplied by the same thing. Right
		//now it looks like it's working.
		if (xChange > 2 && (Math.abs(xChange) * 5 > Math.abs(yChange) * 5))
		{
			direction[0] = "LEFT";
		} else if (xChange < -2
				&& (Math.abs(xChange) * 5 > Math.abs(yChange) * 5))
		{
			direction[0] = "RIGHT";
		}

		else if (yChange > 2 && (Math.abs(yChange) * 5 > Math.abs(xChange) * 5))
		{
			direction[0] = "BACK";
		} else if (yChange < -2
				&& (Math.abs(yChange) * 5 > Math.abs(xChange) * 5))
		{
			direction[0] = "FORWARD";
		}

		moveBuilder.setLength(0);
		// builder.append("x: ");
		moveBuilder.append(direction[0]);
		// builder.append(" y: ");
		// builder.append(direction[1]);

		String directionMoved = moveBuilder.toString();

		//This whole series of if statements just changes the image arrow on the screen
		//as the user moves the device. The color of the arrow may be changed later.
		if (directionMoved.equals("LEFT"))
		{
			displayArrow.setImageResource(R.drawable.left);
		} else if (directionMoved.equals("RIGHT"))
		{
			displayArrow.setImageResource(R.drawable.right);
		} else if (directionMoved.equals("FORWARD"))
		{
			displayArrow.setImageResource(R.drawable.forward);
		} else if (directionMoved.equals("BACK"))
		{
			displayArrow.setImageResource(R.drawable.back);
		}

		//Below is a little tricky. Basically the button text should only change to
		//certain keywords after the user has made his or her pattern once or twice
		//depending.
		password.append(moveBuilder.toString());

		countMoves++;

		if (countMoves == 5)
		{
			if(password.equals(passwordSaved))
			{
			
			   //This would be changed if the program was compiled to work and
			   //integrate with the real system.
			   finish();
			}
			else
			{
				countMoves = 0;
				screenMessage.setText("Wrong Pattern");
			}
		}
	}
		

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// nothing to do here
	}

	// standard activity lifecycle methods...
	protected void onResume()
	{

		super.onResume();

		manager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	protected void onPause()
	{

		super.onPause();

		manager.unregisterListener(this);

	}

}