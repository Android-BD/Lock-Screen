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

public class SetPasswordActivity extends Activity implements
		SensorEventListener
{
	// Directory where data file is stored in application directory

	
	private StringBuilder moveBuilder = new StringBuilder();
	private StringBuilder password = new StringBuilder();
	private String keepTrackOfPassword = null;
	//private final float NOISE = (float) 2.0;
	private float[] history = new float[2];
	private String[] direction = { "NONE" };
	private SensorManager manager = null;
	private Sensor accelerometer = null;
	private int countMoves = 0;
	private ImageView displayArrow = null;
	private Button cancelRetryButton = null;
	private Button continueConfirmButton = null;
	private TextView screenMessage = null;
	private int seriesOfTurns = 0;
	private Boolean tryAgain = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Call a method to check if the user already has a password.
		// If there is a file in Android internal storage then there is no need
		// to set the password right now. Go to the unlock screen. If it is
		// possible
		// to compile with the system at a later date than this step might be
		// different. Not sure if only root devices can attempt a compile of
		// this system
		// or if it's even worth the trouble.

		
		//These following calls just link up variables to actual screen elements in the XML.
		//Strangely enough similar to javascript's findElementById method :)
		displayArrow = (ImageView) findViewById(R.id.imageViewArrow);
		cancelRetryButton = (Button) findViewById(R.id.cancelretrybutton);
		continueConfirmButton = (Button) findViewById(R.id.continueconfirmbutton);
		screenMessage = (TextView) findViewById(R.id.y_axis);

		cancelRetryButton.setText("Cancel");

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

		//The following button actions can be a little confusing but I am
		//going for a certain look that is really similar to the official Android
		//security lock steps. As you progress in setting your pattern the text
		//of the buttons change accordingly. To start off only the cancelRetryButton
		//has text, which is "Cancel." Then once the user is done with the first pattern
		//turn the cancelRetryButton changes to "Retry" and the continueConfirmButton
		//changes to "Continue." Upon pressing continue the user is asked to verify their
		//pattern. Here the cancelRetryButton shows "Cancel" and the continueConfirmButton
		//shows "confirm" when the user has entered a pattern that matched their last one.
		//It's probably a lot easier to see just tinkering with the real lock setup on Android.
		//Give it a try......
		cancelRetryButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{

				
				if (cancelRetryButton.getText().equals("Cancel"))
				{
					finish();
				} else if (cancelRetryButton.getText().equals("Retry"))
				{
					countMoves = 0;
					seriesOfTurns = 0;
					finish();
					startActivity(getIntent());
				}

			}
		});

		continueConfirmButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				if (continueConfirmButton.equals("Continue"))
				{
					// WHEN THIS BUTTON SHOWS CONTINUE
					// THIS STARTS THE SECOND TIME DRAWING THE PATTERN
					// AND IT MUST MATCH THE PATTERN DRAWN BEFORE.
					cancelRetryButton.setText("Cancel");
					screenMessage.setText("Make pattern again to confirm:");

				} else if (continueConfirmButton.equals("Confirm"))
				{
					// CALL UTIL CLASS TO WRITE TO INTERNAL STORAGE AND SAVE
					// PATTERN
					Util.WriteToInternalStorage(password.toString(),
							getApplicationContext());
					finish();
				}

			}
		});
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

		//Below is a little tricky. Bascially the button text should only change to
		//certain keywords after the user has made his or her pattern once or twice
		//depending.
		password.append(moveBuilder.toString());

		countMoves++;

		if (countMoves == 5 && tryAgain == false)
		{
			seriesOfTurns++;
		}

		if (countMoves == 5 && seriesOfTurns == 1)
		{
			cancelRetryButton.setText("Retry");
			continueConfirmButton.setText("Continue");
			screenMessage.setText("Pattern recorded.");
			keepTrackOfPassword = password.toString();

		} else if (countMoves == 5 && seriesOfTurns == 2)
		{
			if (keepTrackOfPassword.equals(password.toString()))
			{
				continueConfirmButton.setText("Confirm");
				screenMessage.setText("Your new unlock pattern:");
			} else
			{
				//Here this means the new pattern the user made didn't match the
				//one they made in the first step!
				
				screenMessage.setText("Try again:");
				cancelRetryButton.setText("Cancel");
				countMoves = 0;
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