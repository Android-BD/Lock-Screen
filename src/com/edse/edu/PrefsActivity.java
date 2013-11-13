package com.edse.edu;

import java.util.regex.Pattern;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity
{

	SharedPreferences prefs;
	Editor myEditor;
	private EditTextPreference ipTextBox, portNumTextBox, textPortNumTextBox;
	private static final String IP_REG_EXPRESSION = "^((1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)\\.){3}(?:1\\d{2}|2[0-4]\\d|25[0-5]|\\d?\\d)$";
	
	private String IP_FROM_PREFS = "ipAddressPref";
	private String PORT_NUM_PREFS = "portNumberPref";
	private String TEXT_PORT_PREFS = "portTexturePref";
	@Override
	/**
	 * The onCreate method handles thing when starting this activity, 
	 * mainly display the activity_settings.xml.
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//I know this method is deprecated, there's no easy way to do it that I've found
		//without using this method or possibly making the program incompatible with anything but 4.0
		addPreferencesFromResource(R.layout.activity_settings);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		ipTextBox = (EditTextPreference) getPreferenceScreen().findPreference(IP_FROM_PREFS);
		portNumTextBox = (EditTextPreference) getPreferenceScreen().findPreference(PORT_NUM_PREFS);
		textPortNumTextBox = (EditTextPreference) getPreferenceScreen().findPreference(TEXT_PORT_PREFS);
		
		
		/*
		
		textPortNumTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) 
            {
            	myEditor = prefs.edit();
                Boolean rtnval = true;
                
                
                if (newValue.toString().length() > 0)
                {
                	int integerCheck = Integer.parseInt(newValue.toString());
                	
                	if(integerCheck > 1024 && integerCheck < 65536)
                	{
                		myEditor.putString(TEXT_PORT_PREFS, Integer.toString(integerCheck));
						myEditor.commit();
                	}
                	else
                   {
                	prefs.edit().remove(TEXT_PORT_PREFS).commit();
        			textPortNumTextBox.setText("");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrefsActivity.this);
                    builder.setTitle(R.string.invalid_input);
                    builder.setMessage(R.string.input_range_texture_error);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                   }
                }
                else
                {
                	portNumTextBox.setText("");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrefsActivity.this);
                    builder.setTitle(R.string.invalid_input);
                    builder.setMessage(R.string.input_range_texture_error);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                }
                
                return rtnval;
            }
        });
		
		
		
		  portNumTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

	            @Override
	            public boolean onPreferenceChange(Preference preference, Object newValue) 
	            {
	            	myEditor = prefs.edit();
	                Boolean rtnval = true;
	                
	                
	                if (newValue.toString().length() > 0)
	                {
	                	int integerCheck = Integer.parseInt(newValue.toString());
	                	
	                	if(integerCheck > 1024 && integerCheck < 65536)
	                	{
	                		myEditor.putString(PORT_NUM_PREFS, Integer.toString(integerCheck));
							myEditor.commit();
	                	}
	                	else
	                {
	                	prefs.edit().remove(PORT_NUM_PREFS).commit();
	        			portNumTextBox.setText("");
	                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrefsActivity.this);
	                    builder.setTitle(R.string.invalid_input);
	                    builder.setMessage(R.string.input_range_error);
	                    builder.setPositiveButton(android.R.string.ok, null);
	                    builder.show();
	                    rtnval = false;
	                }
	                	
	                }else
	                {
	                	portNumTextBox.setText("");
	                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrefsActivity.this);
	                    builder.setTitle(R.string.invalid_input);
	                    builder.setMessage(R.string.input_range_error);
	                    builder.setPositiveButton(android.R.string.ok, null);
	                    builder.show();
	                    rtnval = false;
	                }
	                
	                return rtnval;
	            }
	        });
		
		
		
		
		
		
        ipTextBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) 
            {
            	myEditor = prefs.edit();
                Boolean rtnval = true;
                if (newValue.toString().matches(IP_REG_EXPRESSION) && newValue.toString().length() > 0)
                {
                	
                	myEditor.putString(IP_FROM_PREFS, newValue.toString());
					myEditor.commit();
					
                	
                }
                else
                {
                	prefs.edit().remove(IP_FROM_PREFS).commit();
        			ipTextBox.setText("");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PrefsActivity.this);
                    builder.setTitle(R.string.invalid_input);
                    builder.setMessage(R.string.ip_error_message);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval = false;
                }
                return rtnval;
            }
        });
	}
	*/
}
}