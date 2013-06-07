package edu.wcu.cs.cs495.slider;

import java.util.List;
import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * SliderActivity handles life-cycle events for the Slider Puzzle
 * 
 * @author Zach Scroggs
 * @author Chris Blades
 * @version 29 Oct., 2010
 */
public class SliderActivity extends Activity implements SensorEventListener {
	/** The stored initialization of the SliderView */
	private SliderView slider;	
	/** Request code for Preferences menu button */
	private final static int PREFERENCES = 1;
	/** Request code for Select Picture menu button */
	private final static int SELECTPICTURE = 2;
	/** Request code for About menu button */
	private final static int ABOUT = 3;
	/** Threshold for shuffling on shake */
	private final static int SHUFFLE_ACCEL_THRESHOLD = 500;
	/** Minimum time that has to pass between shakes */
	private final static int ACCEL_POLL_THRESHOLD = 500;
	/** The name of the SliderActivity class, stored */
	private final static String TAG = SliderActivity.class.getName();
	/** Storage for grid height in preferences */
	private int newGridHeight = 4;
	/** Storage for grid width in preferences */
	private int newGridWidth = 3;
	/** Storage for shuffle number in preferences */
	private int newNumShuffles = 20;
	/** image URI pulled from the select picture intent */
	private Uri currImageURI = null;
	/** last X acceleration value */
	private float lastX;
	/** last Y acceleration value */
	private float lastY;
	/** last Z acceleration value */
	private float lastZ;
	/** time of the last shake */
	private long lastAccelUpdate;

	/** 
	 * Called when the activity is first created. 
	 * 
	 * @param savedInstanceState	a Bundle passed to store/retrieve
	 * 								activity state from.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// update slider state
		slider = (SliderView)findViewById(R.id.slider_view);
		updateFromPreferences();

		// listen to accelerometer to implement shake shuffling
		SensorManager sensorMan = 
			(SensorManager)getSystemService(SENSOR_SERVICE);
		// get a list of accelerometer devices
		List<Sensor> accels = 
			sensorMan.getSensorList(Sensor.TYPE_ACCELEROMETER);

		// only register a listener if system has an accelerometer
		if (accels.size() != 0) {
			sensorMan.registerListener((SensorEventListener)this,
					accels.get(0),
					SensorManager.SENSOR_DELAY_UI);
		}
	}

	/**
	 * Restores the state of a program, given its previous state in a Bundle.
	 * 
	 * @param state 	is a Bundle passed to onRestoreInstanceState that will 
	 *                  restore the previous program state residing in the 
	 *                  Bundle.
	 */
	@Override
	public void onRestoreInstanceState(Bundle state) {
		slider.onRestoreInstanceState(state.getParcelable("Slider"));
	}

	/**
	 * Saves the state of a program to the passed bundle.
	 * 
	 * @param state		is a Bundle passed to onSaveInstanceState that will
	 * 					save the current program state into the Bundle.
	 */
	@Override
	public void onSaveInstanceState(Bundle state) {
		Log.i("SliderActivity:onSaveInstanceState()", "going down...");
		state.putParcelable("Slider", slider.onSaveInstanceState());
	}

	/**
	 * onCreateOptionsMenu inflates the menu given the XML file in the 
	 * inflate parameters (in this case slider_menu.xml)
	 * 
	 * @param menu	Menu item to inflate
	 * @return 		true in all cases 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.slider_menu, menu);
		return true;
	}

	/**
	 * onOptionsItemSelected determines what occurs when a MenuItem is selected
	 * in a menu.
	 * 
	 * @param item	a MenuItem object that represents each item in a menu
	 * @return		true in all cases
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.newgame: {
			slider.startGame();
			break;
		}
		case R.id.preferences: {
			Intent i = new Intent(this, Preferences.class);
			startActivityForResult(i, PREFERENCES);
			break;
		}
		case R.id.selectpicture: {
			Intent i = new Intent();  
			i.setType("image/*");  
			i.setAction(Intent.ACTION_GET_CONTENT);  
			startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECTPICTURE);
			break;
		}
		case R.id.about: {
			Intent i = new Intent(this, About.class);
			startActivityForResult(i, ABOUT);
			break;
		}
		}
		return true;
	}

	/**
	 * onActivityResult is called after startActivityForResult finishes, and in 
	 * onActivityResult we determine how our activity was effected by the 
	 * called activity from startActivityForResult.
	 * 
	 * @param requestCode	the integer ID of the menu item
	 * @param resultCode	the integer that symbolizes the result
	 * @param data			an Intent object that contains data passed from the 
	 * 						activity that was called.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * Use the unique integer id to figure our from which activity the
		 * result is coming.
		 */
		switch (requestCode) {
		case PREFERENCES: {
			updateFromPreferences();
			break;
		}
		case SELECTPICTURE: {
			if (data != null) {
				currImageURI = data.getData();
				String filePath = getRealPathFromURI(currImageURI);
				Bitmap picture = BitmapFactory.decodeFile(filePath);
				slider.setPicture(picture);
			}
			break;
		}
		case ABOUT: {
			break;
		}
		default: {
			Log.v(TAG, "Unknown request code: " + requestCode);
			break;
		}
		}
	}

	/**
	 * getRealPathFromURI takes a Uri, and changes it to a canonical file path 
	 * to be used by our SliderView, in order to change our picture if one is 
	 * selected from the Select Picture menu item.
	 * 
	 * @param contentUri	a Uri object containing the Uri of a picture to be
	 * 						changed into a file path.
	 * @return				a String object that contains the file path of the
	 * 						picture.
	 */
	public String getRealPathFromURI(Uri contentUri) {  
		// can post image  
		String [] proj={MediaStore.Images.Media.DATA};  
		Cursor cursor = managedQuery( contentUri,  
				proj, // Which columns to return  
				null, // WHERE clause; which rows to return (all rows)  
				null, // WHERE clause selection arguments (none)  
				null);// Order-by clause (ascending by name)  
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		cursor.moveToFirst();  
		return cursor.getString(column_index); //returns real filepath  
	}  

	/**
	 * updateFromPreferences updates the activity given the state of the 
	 * preferences activity. If the preferences activity has been changed, 
	 * this method will be called to change the SliderActivity.
	 */
	private void updateFromPreferences() {
		/*
		 * Get a SharedPreferences instance that points to the default file that
		 * is used by the preference framework in the given context
		 */
		SharedPreferences prefs =
			PreferenceManager.getDefaultSharedPreferences(
					getApplicationContext());
		String heightValue = prefs.getString("PREF_GRID_HEIGHT", "4");
		String widthValue = prefs.getString("PREF_GRID_WIDTH", "3");
		String numIterations = prefs.getString("PREF_NUM_SHUFFLES", "20");

		try {
			newGridHeight = Integer.parseInt(heightValue);
			newGridWidth = Integer.parseInt(widthValue);
			if (newGridHeight > 1 && newGridWidth > 1) {
				slider.setSize(newGridWidth, newGridHeight);
			}
			newNumShuffles = Integer.parseInt(numIterations);
			slider.changeShuffleNum(newNumShuffles);

		} catch (NumberFormatException ex) {
			Editor editor = prefs.edit();
			editor.putString("PREF_GRID_HEIGHT", "4");
			editor.putString("PREF_GRID_WIDTH", "3");
			editor.commit();
			newGridHeight = 4;
			newGridWidth = 3;
		}
	}

	//////////////////////////////////////////
	// Sensor stuff, for shake shuffling
	//////////////////////////////////////////

	/**
	 * Called when the accuracy of an observed sensor changes.
	 * 
	 * @param sensor the sensor whos accuracy changed
	 * @param accuracy the new accuracy of the sensor
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// DO NOTHING.  our application is very insensitive to accuracy
	}

	/**
	 * Called when the sensor changes in some way, i.e., new sensor values are
	 * available.
	 * 
	 * @param event object describing the new state of or data obtained by the
	 * sensor
	 */
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long currTime = System.currentTimeMillis();
			float deltaTime = currTime - lastAccelUpdate;

			// only check for shake if ACCEL_POLL_THRESHOLD milliseconds have
			// passed, to prevent bogging the app down with lots and lots of 
			// shake checks
			if (deltaTime > ACCEL_POLL_THRESHOLD) {
				lastAccelUpdate = currTime;
				float[] values = event.values;
				//
				// SensorEvent defines the following for an accelerometer:
				// accelX = values[0];
				// accelY = values[1];
				// accelZ = values[2];
				//
				float x = values[0];
				float y = values[1];
				float z = values[2];

				// the change in acceleration
				float deltaAccel = Math.abs((x - lastX) + 
											(y - lastY) +
											(z - lastZ));

				// the rate of change of acceleration
				float slopeAccel = deltaAccel / deltaTime * 
									/*conversion factor*/10000;

				lastX = x;
				lastY = y;
				lastZ = z;

				Log.i(TAG, "slopeAccel: " + slopeAccel);
				if (slopeAccel > SHUFFLE_ACCEL_THRESHOLD) {
					slider.startGame();
				}
			}
		}
	}
}