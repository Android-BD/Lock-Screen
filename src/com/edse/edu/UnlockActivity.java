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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockActivity extends Activity implements SensorEventListener
		
{
	// Directory where data file is stored in application directory

	

	private SensorManager sensorManager;
	private int countMoves = 0;
	private ImageView displayArrow = null;
	private TextView screenMessage = null;
	private String passwordSaved = null;
	private SharedPreferences settings;
	private static final String RECEIVED_PASSWORD = "unlock";
	private Editor myEditor;
	private String getBackupPin = null;
	private int numOfTries = 0;
	private int count = 0;
	private long lastUpdate;
	
	private StringBuilder attemptedPassword = new StringBuilder();
	
	private Button cancelRetryButton = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Is this stuff necessary?
		super.onCreate(savedInstanceState);
		setContentView(R.layout.unlockscreen);
		
		setTitleColor(getResources().getColor(R.color.black));
		
		
		
		
		
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
        

        // Set initial last Update Time
        lastUpdate = System.currentTimeMillis();
        
        // Set up sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		System.out.println("Got here!?!?");

        // Set up cancel/retry button
		cancelRetryButton = (Button)findViewById(R.id.cancelretrybutton);

		cancelRetryButton.setText("Cancel");
		System.out.println("But not here here!?!?");


		



		
		cancelRetryButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				

				if (cancelRetryButton.getText().equals("Cancel"))
				{
					finish();
				} else if (cancelRetryButton.getText().equals("Retry"))
				{
					//continueConfirmButton.setText("");
					//continueConfirmButton.setEnabled(false);
					//displayArrow.setImageResource(0);
					cancelRetryButton.setText("Cancel");
					count = 0;
					screenMessage.setText(R.string.unlock_pattern); // TODO: Remove?
					attemptedPassword.setLength(0);
					numOfTries++;
					startActivity(getIntent());
					sensorManager.registerListener(UnlockActivity.this,
							sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);
				}

			}
		});
	
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			getAccelerometer(event);
		}

	}

	private void getAccelerometer(SensorEvent event)
	{
		String direction = null;
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 1.5) //
		{
			if (actualTime - lastUpdate < 900)
			{
				return;
			}
			lastUpdate = actualTime;
			if(x > .5 && (Math.abs(x)*2 > Math.abs(y)*2))
			{
				direction = "Right";
				displayArrow.setImageResource(R.drawable.newright);
				Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
				
			}
			else if(x < -.5 && (Math.abs(x)*2 > Math.abs(y)*2))
			{
				direction = "Left";
				displayArrow.setImageResource(R.drawable.newleft);
				Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			}
			else if(y > .5 && (Math.abs(y)*2 > Math.abs(x)*2))
			{
				direction = "Forward";
				displayArrow.setImageResource(R.drawable.newforward);
				Toast.makeText(this, "Forward", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			}
			else if(y < -.5 && (Math.abs(y)*2 > Math.abs(x)*2))
			{
				direction = "Back";
				displayArrow.setImageResource(R.drawable.newback);
				Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			}
			count++;
			
			
			/*****************************************************************************/
			// If direction != null, append to the current attemptedPassword try
			
			// If it is the fourth motion, check it against saved password
			// 		Check that weird NULL case, automatically fail the validation
			// 		Otherwise, check against the saved password
			//			run the current attemptedPassword through the m5 encoder to get the string, compare against saved password
			//				If correct, exit activity
			
			// Else If it is the Fifth attempt, fail out to PIN entry
			//		Unregister the accelerometer listener
			//		Pop-up requesting the PIN if available
			//		If PIN is incorrect or D.N.E., lock for 5 minutes?  Go back to unlock motion controls?
			/*****************************************************************************/

			
			if (numOfTries < 6)
			{
				// If direction != null, append to the current attemptedPassword try
				if (direction != null)
				{
					attemptedPassword.append(direction);
				}
				
				// If it is the fourth motion, check it against saved password
				if (count % 4 == 0) 
				{
					sensorManager.unregisterListener(this);
					
					
					// Check for weird NULL case, automatically fail the validation
					//StringBuilder display = attemptedPassword;
					
//					String check = Util.splitCamelCase(display.toString());
//					int commaCount = 0;
//					for(int i = 0; i < check.length(); i++)
//					{
//						char ch = check.charAt(i);
//						if(ch == ',')
//						{
//							commaCount++;
//						}
//					}
//					
//					
//					if(commaCount != 3)
//					{
//						// Wrong password
//						attemptedPassword.setLength(0);
//						cancelRetryButton.setText("Retry");
//						//displayArrow.setImageResource(0);
//						screenMessage.setText("Incorrect.  Please try again.");
//					}
//					// Otherwise, check against the saved password
//					else
					//{
						//  run the current attemptedPassword through the m5 encoder to get the string, compare against saved password
						String encryptedAttemptedPassword = Util.md5(attemptedPassword.toString());

						if (encryptedAttemptedPassword.toString().equals(passwordSaved.toString()))
						{
							// Password correct, exit application
							System.exit(0);
						}
						else
						{
							// TODO: This could be cleaner...if I made the if(commaCount == 3), then could just put those things in the body of the if after the if block
							
							// Wrong password
							attemptedPassword.setLength(0);
							cancelRetryButton.setText("Retry");
							//displayArrow.setImageResource(0);
							screenMessage.setText("Incorrect.  Please try again.");
						}
					//}
					
					
					numOfTries++;
					sensorManager.registerListener(this,
							sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);

				}
			}
			// Else if it is the fifth attempt, fail out to the PIN entry
			else 
			{
				// Unregister the accelerometer listener
				sensorManager.unregisterListener(this);

				// Pop-up requesting the PIN if available
				if (getBackupPin != null)
				{
					Dialog dialog;
					final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
					final EditText passTextBox = new EditText(this);
		        	dialogBuilder.setView(passTextBox);
					dialogBuilder.setMessage("Enter your PIN: ");
					dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							String attemptedPin = passTextBox.getEditableText().toString();
							
							if (attemptedPin.toString().equals(getBackupPin.toString()))
							{
								//Pin correct, exit application
								System.exit(0);
							}
							else
							{
								// Incorrect Pin
								cancelRetryButton.setText("Retry");
								screenMessage.setText("Incorrect PIN.  Please try again.");
							}
						}
					});
					dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
						}
					});
					dialog = dialogBuilder.create();
					dialog.show();
				}
				
				// If PIN is incorrect, cancelled or D.N.E., lock for 5 minutes?  Go back to unlock motion controls?  Go back to motion controls.
				numOfTries = 5;
				
				sensorManager.registerListener(this,
						sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_NORMAL);
			}	
		}
		
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause()
	{
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
		//finish();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		
		inflater.inflate(R.menu.options_menu,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		
		case R.id.settings:
			//go to settings prefs.
			startActivity(new Intent(this,
					PrefsActivity.class));
			return true;
		
		case R.id.about:
			startActivity(new Intent(this,
					AboutActivity.class));
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	


}