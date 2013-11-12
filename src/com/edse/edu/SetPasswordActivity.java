package com.edse.edu;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordActivity extends Activity implements SensorEventListener
{
	private SensorManager sensorManager;
	private boolean color = false;
	private View view;
	private long lastUpdate;
	private int count = 0;
	private int turn = 1;
	private StringBuilder password = new StringBuilder();
	private StringBuilder secondConfirmPassword = new StringBuilder();

	private Button cancelRetryButton = null;
	private Button continueConfirmButton = null;
	private TextView screenMessage = null;
	private ImageView displayArrow = null;

	long lastDown = 0;
	long lastDuration = 0;
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		try
		{
			Util.CheckInternalStorage(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			//	WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword);
		//view = findViewById(R.id.y_axis);
		
		
		setTitleColor(getResources().getColor(R.color.black));
		screenMessage = (TextView)findViewById(R.id.x_axis);
		screenMessage.setText(R.string.unlock_pattern);
		cancelRetryButton = (Button)findViewById(R.id.cancelretrybutton);
		continueConfirmButton = (Button)findViewById(R.id.continueconfirmbutton);
		continueConfirmButton.setEnabled(false);
		displayArrow = (ImageView)findViewById(R.id.imageViewArrow);
		cancelRetryButton.setText("Cancel");
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
					count = 0;
					turn = 1;
					screenMessage.setText(R.string.unlock_pattern);
					password.setLength(0);
					secondConfirmPassword.setLength(0);
					startActivity(getIntent());
					sensorManager.registerListener(SetPasswordActivity.this,
							sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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
					
					screenMessage.setText("Pattern to confirm:" + "\n " + Util.splitCamelCase(password.toString()));
					cancelRetryButton.setText("Cancel");
					continueConfirmButton.setText("");
					continueConfirmButton.setEnabled(false);
					
					turn = -1;
					sensorManager.registerListener(SetPasswordActivity.this,
							sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);
					
				} else if (continueConfirmButton.getText().equals("Confirm"))
				{
					Util.WriteToInternalStorage(Util.md5(password.toString()), getApplicationContext());
					Toast.makeText(getApplicationContext(),"Your password has been recorded!", Toast.LENGTH_SHORT).show();
					finish();
					
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
		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 200)
			{
				return;
			}
			lastUpdate = actualTime;
			if(x > 8 && (Math.log(Math.abs(x)) > Math.log(Math.abs(y))))
			{
				direction = "Right";
				displayArrow.setImageResource(R.drawable.newright);
				Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
			
				
			}
			else if(x < -8 && (Math.log((Math.abs(x))) > Math.log(Math.abs(y))))
			{
				direction = "Left";
				displayArrow.setImageResource(R.drawable.newleft);
				Toast.makeText(this, "Left", Toast.LENGTH_SHORT).show();
				
			}
			else if(y > 8 && (Math.log(Math.abs(y)) > Math.log(Math.abs(x))))
			{
				direction = "Forward";
				displayArrow.setImageResource(R.drawable.newforward);
				Toast.makeText(this, "Forward", Toast.LENGTH_SHORT).show();
			}
			else if(y < -8 && (Math.log(Math.abs(y)) > Math.log(Math.abs(x))))
			{
				direction = "Back";
				displayArrow.setImageResource(R.drawable.newback);
				Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
			}
			count++;
			Toast.makeText(this, "Device was shuffed " + count + " " + turn, Toast.LENGTH_SHORT).show();
			
			
			if(turn == 1 && direction != null)
			{
			password.append(direction);
			//Toast.makeText(getApplicationContext(), password.toString(), Toast.LENGTH_SHORT);
			}
			
			if(turn == -1 && direction != null)
			{
				secondConfirmPassword.append(direction);
				
			}
			
			if(count%4 == 0 && turn == 1)
			{
				
				sensorManager.unregisterListener(this);
				StringBuilder display = password;
				
				//VERY RARE CASE WHERE SOMETHING RETURNS NULL OR ONLY THREE DIRECTIONS SHOW UP AS THE RESULT.
				String check = Util.splitCamelCase(display.toString());
				int commaCount = 0;
				for(int i = 0; i < check.length(); i++)
				{
					char ch = check.charAt(i);
					if(ch == ',')
					{
						commaCount++;
					}
				}
				
				
				if(commaCount == 3)
				{
				Toast.makeText(this, display.toString(), Toast.LENGTH_SHORT).show();
				screenMessage.setText("");
				screenMessage.setText("Pattern Recorded. \n " + Util.splitCamelCase(display.toString()));
				continueConfirmButton.setEnabled(true);
				continueConfirmButton.setText("Continue");
				cancelRetryButton.setText("Retry");
				displayArrow.setImageResource(0);
				}
				else
				{
					
				}
				
				
			}
			
			if(count%4==0 && turn == -1)
			{
				sensorManager.unregisterListener(this);
				if(secondConfirmPassword.toString().equals(password.toString()))
				{
					screenMessage.setText("Your new unlock pattern:");
					continueConfirmButton.setText("Confirm");
					Toast.makeText(getApplicationContext(), password.toString(), Toast.LENGTH_SHORT).show();
					continueConfirmButton.setEnabled(true);
					
				}
				else
				{
					screenMessage.setTextColor(Color.RED);
					screenMessage.setText("Try again:\n" + Util.splitCamelCase(password.toString()));
					
					secondConfirmPassword.setLength(0);
					displayArrow.setImageResource(0);
					turn = -1;
					
					sensorManager.registerListener(this,
							sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
							SensorManager.SENSOR_DELAY_NORMAL);
					
				}
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
	protected void onStop()
	{
		System.exit(0);
	}
	@Override
	protected void onPause()
	{
		// unregister listener
		super.onPause();
		sensorManager.unregisterListener(this);
		finish();
		
	}
}