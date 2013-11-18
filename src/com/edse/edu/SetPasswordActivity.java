package com.edse.edu;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordActivity extends Activity implements
		SensorEventListener
{
	private SensorManager sensorManager;
	private boolean color = false;
	private View view;
	private long lastUpdate;
	private int count = 0;
	public static String globalPassword = null;
	private StringBuilder password = new StringBuilder();
	private StringBuilder secondConfirmPassword = new StringBuilder();

	private Button cancelRetryButton = null;
	private Button continueConfirmButton = null;
	private TextView screenMessage = null;
	private ImageView displayArrow = null;
	private static int FIRST_TURN = 1;
	private static int SECOND_TURN = -1;
	private int turn = FIRST_TURN;
	private static Activity activity = null;
	long lastDown = 0;
	long lastDuration = 0;
	private boolean instructionTouched = false;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		activity = this;
		try
		{
			Util.CheckInternalStorage(this);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword);

		setTitleColor(getResources().getColor(R.color.black));
		screenMessage = (TextView) findViewById(R.id.x_axis);
		screenMessage.setText(R.string.unlock_pattern);
		cancelRetryButton = (Button) findViewById(R.id.cancelretrybutton);
		continueConfirmButton = (Button) findViewById(R.id.continueconfirmbutton);
		continueConfirmButton.setEnabled(false);
		displayArrow = (ImageView) findViewById(R.id.imageViewArrow);
		cancelRetryButton.setText("Cancel");

		final View topLevelLayout = findViewById(R.id.top_layout);
		topLevelLayout.setVisibility(View.VISIBLE);

		topLevelLayout.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{

				v.setVisibility(View.INVISIBLE);

				instructionTouched = true;
				return true;

			}

		});

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();

		cancelRetryButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{

				if (cancelRetryButton.getText().equals("Cancel"))
				{
					finish();
				} else if (cancelRetryButton.getText().equals("Retry"))
				{
					continueConfirmButton.setText("");
					continueConfirmButton.setEnabled(false);
					displayArrow.setImageResource(0);
					cancelRetryButton.setText("Cancel");
					count = 0;
					turn = FIRST_TURN;
					screenMessage.setText(R.string.unlock_pattern);
					password.setLength(0);
					secondConfirmPassword.setLength(0);
					startActivity(getIntent());
					sensorManager
							.registerListener(
									SetPasswordActivity.this,
									sensorManager
											.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_NORMAL);
				}

			}
		});

		continueConfirmButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{

				if (continueConfirmButton.getText().equals("Continue"))
				{

					screenMessage.setText("Pattern to confirm:" + "\n "
							+ Util.splitCamelCase(password.toString()));
					cancelRetryButton.setText("Cancel");
					continueConfirmButton.setText("");
					continueConfirmButton.setEnabled(false);

					turn = SECOND_TURN;
					sensorManager
							.registerListener(
									SetPasswordActivity.this,
									sensorManager
											.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
									SensorManager.SENSOR_DELAY_NORMAL);

				} else if (continueConfirmButton.getText().equals("Confirm"))
				{
					try
					{
						Util.WriteToInternalStorage(password.toString(),
								getApplicationContext());
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(),
							"Your password has been recorded!",
							Toast.LENGTH_SHORT).show();
					finish();

				}

			}
		});
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
				&& instructionTouched == true)
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
		// if(instructionTouched == true)
		// {
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
				displayArrow.setImageResource(R.drawable.newright);
				Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);

			} else if (x < -.75 && (Math.abs(x) * 2 > Math.abs(y) * 2))
			{
				direction = "Left";
				displayArrow.setImageResource(R.drawable.newleft);
				Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			} else if (y > .75 && (Math.abs(y) * 2 > Math.abs(x) * 2))
			{
				direction = "Forward";
				displayArrow.setImageResource(R.drawable.newforward);
				Toast.makeText(this, "Forward", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			} else if (y < -.75 && (Math.abs(y) * 2 > Math.abs(x) * 2))
			{
				direction = "Back";
				displayArrow.setImageResource(R.drawable.newback);
				Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
				sensorManager.unregisterListener(this);
			}
			count++;

			if (displayArrow.getDrawable() != null)
			{
				Toast.makeText(this,
						"Device was shuffed " + count + " " + turn,
						Toast.LENGTH_SHORT).show();

				if (turn == FIRST_TURN && direction != null)
				{
					password.append(direction);
					// Toast.makeText(getApplicationContext(),
					// password.toString(), Toast.LENGTH_SHORT);
					sensorManager.registerListener(this, sensorManager
							.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);
				}

				if (turn == SECOND_TURN && direction != null)
				{
					secondConfirmPassword.append(direction);
					sensorManager.registerListener(this, sensorManager
							.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);

				}

				if (count % 4 == 0 && turn == FIRST_TURN)
				{

					sensorManager.unregisterListener(this);

					StringBuilder display = password;

					Toast.makeText(this, display.toString(), Toast.LENGTH_SHORT)
							.show();
					screenMessage.setText("");
					screenMessage.setText("Pattern Recorded. \n "
							+ Util.splitCamelCase(display.toString()));
					continueConfirmButton.setEnabled(true);
					continueConfirmButton.setText("Continue");
					cancelRetryButton.setText("Retry");
					displayArrow.setImageResource(0);

				}

				if (count % 4 == 0 && turn == SECOND_TURN)
				{
					sensorManager.unregisterListener(this);
					if (secondConfirmPassword.toString().equals(
							password.toString()))
					{
						screenMessage.setText("Your new unlock pattern:");
						continueConfirmButton.setText("Confirm");
						Toast.makeText(getApplicationContext(),
								password.toString(), Toast.LENGTH_SHORT).show();
						continueConfirmButton.setEnabled(true);
						globalPassword = secondConfirmPassword.toString();
					} else
					{
						screenMessage.setTextColor(Color.RED);
						screenMessage.setText("Try again:\n"
								+ Util.splitCamelCase(password.toString()));

						secondConfirmPassword.setLength(0);
						displayArrow.setImageResource(0);
						turn = SECOND_TURN;

						sensorManager.registerListener(this, sensorManager
								.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
								SensorManager.SENSOR_DELAY_NORMAL);

					}
				}

			}

			else
			{
				Toast.makeText(this, "Sensor interference. Try again",
						Toast.LENGTH_SHORT).show();
				continueConfirmButton.setText("");
				continueConfirmButton.setEnabled(false);
				displayArrow.setImageResource(0);
				cancelRetryButton.setText("Cancel");
				count--;
				screenMessage.setText(R.string.unlock_pattern);
				password.setLength(0);
				secondConfirmPassword.setLength(0);
				startActivity(getIntent());
				sensorManager.registerListener(SetPasswordActivity.this,
						sensorManager
								.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_NORMAL);

			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause()
	{
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
		// finish();

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d("IN ACTIVITY RESULT SET PASSWORD ACTIVITY",
				Integer.toString(resultCode));
		if (resultCode == 0)
		{
			finish();
		}
	}

}