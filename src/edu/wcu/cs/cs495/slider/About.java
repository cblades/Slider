package edu.wcu.cs.cs495.slider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Activity displaying info about the slider puzzle game.
 * 
 * @author Zach Scroggs
 * @author Chris Blades
 * @version 29 Oct., 2010
 *
 */
public class About extends Activity{
	/** Ok button, clicked to exit activity */
	private Button okButton;
	
	/** Listener for button clicks */
	private OnClickListener listener;

	/**
	 * Create a new About activity and restore any previous state.
	 * 
	 * @param savedInstanceState state info from a previous about
	 * activity
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        okButton = (Button) findViewById(R.id.ok_button);
        listener = new OnClickListener() {
			/**
			 * Called when the specified view is clicked.
			 * 
			 * @param v The view that was clicked.
			 */
			public void onClick(View v) {
				if (v == okButton) {
	                About.this.setResult(Activity.RESULT_OK);
				}
				finish();
			}
		};
		
		okButton.setOnClickListener(listener);        
    }
}
