package edu.wcu.cs.cs495.slider;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Represents a single slide puzzle piece.  Maintains current and desired
 * position as well as information used to draw the Tile.
 * 
 * @author Chris Blades
 * @author Zach Scroggs
 * @version 24/10/2010
 */
class Tile implements Serializable {
	
	/**
	 * X coordinate where the tiles should be drawn measured in tiles
	 * <b><i>not pixels</i></b>
	 */
	private int dstX;

	/**
	 * Y coordinate where the tiles should be drawn measured in tiles
	 * <b><i>not pixels</i></b>
	 */
	private int dstY;

	/**
	 * X coordinate of the tiles correct position measured in tiles
	 * <b><i>not pixels</i></b>
	 */
	private int srcX;
	/**
	 * Y coordinate of the tiles correct position measured in tiles
	 * <b><i>not pixels</i></b>
	 */
	private int srcY;
	
	/**
	 * Width of this tile, in pixels
	 */
	private int tileWidth;
	
	/**
	 * Height of this tile, in pixels
	 */
	private int tileHeight;
	
	/**
	 * Wether or not this is the blank tile
	 */
	private boolean blank;

	/**
	 * Create a new Tile with the given correct and current position and
	 * picture portion.
	 * 
	 * @param x correct and current X coordinate, in tiles not pixels
	 * @param y correct and current Y coordinate, in tiles not pixels
	 * @param src what portion of the picture to draw for this tile.
	 */
	public Tile(int x, int y) {
		srcX = x;
		dstX = x;

		srcY = y;
		dstY = y;

		blank = false;
	}

	/**
	 * Draw this tile to the given canvas at the current position.
	 * @param canvas object to draw to
	 */
	public void draw(Canvas canvas, Bitmap picture) {
		Log.i("Tile:draw()", "blank: " + blank);
		Paint paint = new Paint();
		/**
		 * construct Rect to define where the tile should be drawn based
		 * on pixels and what portion of the picture to draw
		 */
		Rect dst = new Rect(/*left*/tileWidth * dstX, 
				/*top*/tileHeight * dstY,
				/*right*/tileWidth * (dstX + 1),
				/*bottom*/tileHeight * (dstY + 1));
		
		Rect src = new Rect(/*left*/tileWidth * srcX,
							/*top*/tileHeight * srcY,
							/*right*/tileWidth * (srcX + 1),
							/*bottom*/tileHeight * (srcY + 1));
		
		// if this is blank, just draw a blank rectangle
		if (blank) {
			canvas.drawRect(dst, paint);
			// draw the tile	
		} else {
			canvas.drawBitmap(/*bitmap*/picture, /*what to draw*/src, 
					/*where to draw*/dst, paint);
		}
	}

	/**
	 * Swap the position of this tile with the given tile.
	 * @param other the tile to swap with
	 */
	public void swap(Tile other) {
		int tempX = this.srcX;
		int tempY = this.srcY;
		
		this.srcX = other.srcX;
		this.srcY = other.srcY;
		
		other.srcX = tempX;
		other.srcY = tempY;
	}

	/**
	 * Set if this tile is blank or not.
	 * @param blank wether this tile is blank
	 */
	public void setBlank(boolean blank) {
		this.blank = blank;
	}
	public int getDstX() {
		return dstX;
	}

	public void setDstX(int dstX) {
		this.dstX = dstX;
	}

	public int getDstY() {
		return dstY;
	}

	public void setDstY(int dstY) {
		this.dstY = dstY;
	}

	public int getSrcX() {
		return srcX;
	}

	public void setSrcX(int srcX) {
		this.srcX = srcX;
	}

	public int getSrcY() {
		return srcY;
	}

	public void setSrcY(int srcY) {
		this.srcY = srcY;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public boolean isBlank() {
		return blank;
	}
	/**
	 * Compare two tiles for equality based only on destination coordinates.
	 * @param o Object to compare to
	 */
	@Override
	public boolean equals(Object o) {
		// only compare with other Tiles
		if (o instanceof Tile) {
			Tile other = (Tile)o;
			if (this.dstX == other.dstX && this.dstY == other.dstY) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return if this Tile is in it's correct position or not, i.e., if the
	 * current position matches the correct position.
	 * @return true if this tile is in the correct position, false
	 * otherwise.
	 */
	public boolean correct() {
		return (dstX == srcX) && (dstY == srcY);
	}
}