package com.citex.dragonisland.core.util;

import com.citex.android.free.dragonisland.R;
import com.citex.dragonisland.android.MainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Dialog.java
 * Helper functions for displaying dialogs.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
