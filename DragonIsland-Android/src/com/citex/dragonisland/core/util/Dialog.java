package com.citex.dragonisland.core.util;

import com.citex.android.free.dragonisland.R;
import com.citex.dragonisland.android.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Helper functions for displaying dialogs.
 * @author Lawrence Schmid
 */
public class Dialog {

	/**
	 * Shows android exit game dialog.
	 */
	public static void showExitDialog (final MainActivity mainActivity) {
		
		// Create exit dialog.
		AlertDialog.Builder dialog = new AlertDialog.Builder(mainActivity.mContext);
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.setTitle(R.string.quit_title);
		dialog.setMessage(R.string.quit_message);
		
		// Add event listeners.
		dialog.setPositiveButton(R.string.quit_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Exit game.
						mainActivity.onDestroy();
						mainActivity.finish(); 
				        System.exit(2);
					}
				});
		dialog.setNegativeButton(R.string.quit_no, null);
		
		// Show dialog.
		dialog.show();
	}
	
	
}
