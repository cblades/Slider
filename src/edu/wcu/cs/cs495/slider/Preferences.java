package edu.wcu.cs.cs495.slider;

import android.os.Bundle;
import android.preference.PreferenceActivity;
/**
 * Preferences screen for slider puzzle
 * 
 * @author Zach Scroggs
 * @author Chris Blades
 * @version 29 Oct, 2010
 *
 */
public class Preferences extends PreferenceActivity {
	
	/** 
	 * Called when this activity is first created
	 * 
	 * @param savedInstanceState any state that was saved across activity
	 * restarts
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
