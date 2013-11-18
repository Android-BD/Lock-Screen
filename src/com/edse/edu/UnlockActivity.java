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
import android.text.Editable;
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

	private SensorManager sensorManagerUnlock;
	private ImageView displayArrowUnlock = null;
	private TextView messageUnlock = null;
	private String passwordSaved = null;
	private SharedPreferences settings;
	private static final String RECEIVED_PASSWORD = "unlock";
	private String getBackupPin = null;
	private int numOfTries = 0;
	private int count = 0;
	private long lastUpdate;

	private StringBuilder attemptedPassword = new StringBuilder();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.unlockscreen);

		setTitleColor(getResources().getColor(R.color.black));

		displayArrowUnlock = (ImageView) findViewById(R.id.imageViewUnlock);
		messageUnlock = (TextView) findViewById(R.id.xt_axis);

		// get the password passed from the SetPasswordActivity if its already
		// in internal
		// storage. It should look something like $12XJUY672SQ90JNML8912

		// Getting object's properties and assigning it to the passwordSaved
		// variable.
		Intent intent = getIntent();
		passwordSaved = intent.getStringExtra(RECEIVED_PASSWORD);

		// getting shared preferences settings from prefs screen.
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		getBackupPin = settings.getString("passBackupPref", null);
		if (getBackupPin == null)
		{
			Toast.makeText(
					this,
					"A PIN does not exist.\nPlease enter it in the settings menu.",
					Toast.LENGTH_LONG).show();
		}

		sensorManagerUnlock = (SensorManager) getSystemService(SENSOR_SERVICE);

		lastUpdate = System.currentTimeMillis();

	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			try
			{
				getAccelerometer(event);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void getAccelerometer(SensorEvent event) throws Exception
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
		if (accelationSquareRoot >= 1.7) //
		{
			if (actualTime - lastUpdate < 900)
			{
				return;
			}
			lastUpdate = actualTime;
			if (x > .75 && (Math.abs(x) * 2 > Math.abs(y) * 2))
			{
				direction = "Right";
				displayArrowUnlock.setImageResource(R.drawable.newright);
				Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
				sensorManagerUnlock.unregisterListener(this);

			} else if (x < -.75 && (Math.abs(x) * 2 > Math.abs(y) * 2))
			{
				direction = "Left";
				displayArrowUnlock.setImageResource(R.drawable.newleft);
				Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
				sensorManagerUnlock.unregisterListener(this);
			} else if (y > .75 && (Math.abs(y) * 2 > Math.abs(x) * 2))
			{
				direction = "Forward";
				displayArrowUnlock.setImageResource(R.drawable.newforward);
				Toast.makeText(this, "Forward", Toast.LENGTH_SHORT).show();
				sensorManagerUnlock.unregisterListener(this);
			} else if (y < -.75 && (Math.abs(y) * 2 > Math.abs(x) * 2))
			{
				direction = "Back";
				displayArrowUnlock.setImageResource(R.drawable.newback);
				Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
				sensorManagerUnlock.unregisterListener(this);
			}
			count++;

			/*****************************************************************************/
			// If direction != null, append to the current attemptedPassword try

			// If it is the fourth motion, check it against saved password
			// Check that weird NULL case, automatically fail the validation
			// Otherwise, check against the saved password
			// run the current attemptedPassword through the m5 encoder to get
			// the string, compare against saved password
			// If correct, exit activity

			// Else If it is the Fifth attempt, fail out to PIN entry
			// Unregister the accelerometer listener
			// Pop-up requesting the PIN if available
			// If PIN is incorrect or D.N.E., lock for 5 minutes? Go back to
			// unlock motion controls?
			/*****************************************************************************/

			if (displayArrowUnlock.getDrawable() != null)
			{
				if (numOfTries < 3)
				{
					// If direction != null, append to the current
					// attemptedPassword try
					if (direction != null)
					{
						attemptedPassword.append(direction);
						sensorManagerUnlock
								.registerListener(
										this,
										sensorManagerUnlock
												.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_NORMAL);
					}

					// If it is the fourth motion, check it against saved
					// password
					if (count % 4 == 0)
					{
						sensorManagerUnlock.unregisterListener(this);

						// run the current attemptedPassword through the m5
						// encoder to get the string, compare against saved
						// password
						Toast.makeText(this, attemptedPassword.toString(),
								Toast.LENGTH_SHORT).show();
						String encryptedAttemptedPassword = Util
								.md5(attemptedPassword.toString().trim());

						// Toast.makeText(getApplicationContext(),
						// encryptedAttemptedPassword +"\n" + passwordSaved,
						// Toast.LENGTH_LONG).show();
						Log.d("Encryptedpassrecent", encryptedAttemptedPassword);
						Log.d("savedEncrypted", passwordSaved);
						if (encryptedAttemptedPassword.equals(passwordSaved))
						{
							// Password correct, exit application
							Log.d("DONE", "MATCH");
							setResult(0);
							finish();

						} else
						{
							// TODO: This could be cleaner...if I made the
							// if(commaCount == 3), then could just put those
							// things in the body of the if after the if block

							// Wrong password
							attemptedPassword.setLength(0);
							messageUnlock.setTextColor(Color.RED);
							messageUnlock
									.setText("Incorrect.  Please try again.");
						}

						numOfTries++;
						Toast.makeText(getApplicationContext(),
								"numofTries = " + numOfTries,
								Toast.LENGTH_SHORT).show();
						sensorManagerUnlock
								.registerListener(
										this,
										sensorManagerUnlock
												.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_NORMAL);

					}
				}
				// Else if it is the fifth attempt, fail out to the PIN entry
				else
				{
					// Unregister the accelerometer listener
					sensorManagerUnlock.unregisterListener(this);

					// Pop-up requesting the PIN if available
					if (getBackupPin != null)
					{

						final EditText passTextBox = new EditText(this);
						final AlertDialog pinDialog = new AlertDialog.Builder(
								this).setView(passTextBox)
								.setTitle("Enter your pin.")
								.setPositiveButton("Ok", null)
								.setNegativeButton("Cancel", null).create();
						pinDialog
								.setOnShowListener(new DialogInterface.OnShowListener()
								{

									@Override
									public void onShow(DialogInterface dialog)
									{
										Button okayButton = pinDialog
												.getButton(AlertDialog.BUTTON_POSITIVE);
										okayButton
												.setOnClickListener(new View.OnClickListener()
												{

													@Override
													public void onClick(View v)
													{
														// TODO Auto-generated
														// method stub
														Editable attemptedPin = passTextBox
																.getEditableText();

														if (attemptedPin
																.toString()
																.equals(getBackupPin
																		.toString()))
														{
															finish();
														} else
														{
															passTextBox
																	.setError("Incorrect pin! Please try again.");
														}

													}
												});

									}
								});

						// If PIN is incorrect, cancelled or D.N.E., lock for 5
						// minutes? Go back to unlock motion controls? Go back
						// to motion controls.
						numOfTries = 0;
						attemptedPassword.setLength(0);
						sensorManagerUnlock
								.registerListener(
										this,
										sensorManagerUnlock
												.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										SensorManager.SENSOR_DELAY_NORMAL);
					}

				}
			} else
			{
				Toast.makeText(this, "Sensor interference. Try again",
						Toast.LENGTH_SHORT).show();

				displayArrowUnlock.setImageResource(0);

				count--;

				attemptedPassword.setLength(0);

				startActivity(getIntent());
				sensorManagerUnlock.registerListener(UnlockActivity.this,
						sensorManagerUnlock
								.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_NORMAL);

			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManagerUnlock
				.registerListener(this, sensorManagerUnlock
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause()
	{
		// unregister listener
		super.onPause();
		sensorManagerUnlock.unregisterListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		case R.id.settings:
			// go to settings prefs.
			startActivity(new Intent(this, PrefsActivity.class));
			return true;

		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}