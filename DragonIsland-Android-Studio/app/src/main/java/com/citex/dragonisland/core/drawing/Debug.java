package com.citex.dragonisland.core.drawing;

import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * Debug.java
 * Helper functions for drawing debug output.
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

public class Debug {

	/** Main thread. */
	private Main mMain;
	
	/**
	 * Initialises a Debug object.
	 * @param main Main thread.
	 */
	public Debug(Main main) {		
		mMain = main;		
	}
		
	/**
	 * Draws the debug information.
	 * @param g Graphics context
	 */
	public void draw(Object g, int x, int y) {

		// Level.
		Color color = new Color(0,0,0);	
		Header header = mMain.getLevel().getHeader();
		mMain.getGameFont(3).drawString(g, "width: " + header.width, x, y, color);
		mMain.getGameFont(3).drawString(g, "height: " + header.height, x, y + 11, color);
		mMain.getGameFont(3).drawString(g, "camera x: " + (int)mMain.getCamera().x, x, y + 22, color);
		mMain.getGameFont(3).drawString(g, "camera y: " + (int)mMain.getCamera().y, x, y + 33, color);
		mMain.getGameFont(3).drawString(g, "background: [" + header.bg[0] + "," + header.bg[1] + "," + header.bg[2] + "]", x, y + 44, color);
		mMain.getGameFont(3).drawString(g, "tileset: [" + header.tileset0 + "," + header.tileset16 + "," + header.tileset32 + "]", x, y + 55, color);

		// Player.
		x += 120;
		Player player = mMain.getCurrentPlayer();
		mMain.getGameFont(3).drawString(g, "player x: " + (int)player.getPosition().x, x, y, color);
		mMain.getGameFont(3).drawString(g, "player y: " + (int)player.getPosition().y, x, y + 11, color);
		mMain.getGameFont(3).drawString(g, "velocity x: " + String.format("%.3g%n", player.getVelocity().x), x, y + 22, color);
		mMain.getGameFont(3).drawString(g, "velocity y: " + String.format("%.3g%n", player.getVelocity().y), x, y + 33, color);
		mMain.getGameFont(3).drawString(g, "animation: " + player.getAnimationState(), x, y + 44, color);
		mMain.getGameFont(3).drawString(g, "memory: " + Long.toString(Runtime.getRuntime().totalMemory()), x, y + 55, color);

	}
	
}
