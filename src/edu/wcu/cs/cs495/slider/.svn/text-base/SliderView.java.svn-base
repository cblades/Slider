package edu.wcu.cs.cs495.slider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Represents a slide puzzle game board.  Handles displaying and managing a 
 * slide puzzle and accepting user input.
 * 
 * @author Zach Scroggs
 * @author Chris Blades
 * @version 29 Oct., 2010
 */
public class SliderView extends View implements Serializable {

	/** Default number of horizontal tiles */
	private static final int DEFAULT_WIDTH = 3;
	
	/** Default number of vertical tiles */
	private static final int DEFAULT_HEIGHT = 4;
	
	/** Default picture to use */
	private static final int DEFAULT_PIC_ID = R.drawable.tiger;
	
	/** Default number of times to move when randomizing */
	private static final int DEFAULT_SHUFFLE_ITERATIONS = 20;
	
	///////////////////////
	// State save keys
	//////////////////////
	
	/** bundle key for width */
	private static final String WIDTH_KEY = "width";
	
	/** bundle key for height */
	private static final String HEIGHT_KEY = "height";
	
	/** bundle key for picture */
	private static final String BITMAP_KEY = "bitmap";
	
	/** bundle key for tiles list */
	private static final String TILES_KEY = "tiles";
	
	/** bundle key for won */
	private static final String WON_KEY = "won";
	
	/** bundle key for the blank key */
	private static final String BLANK_KEY = "blank";

	///////////////////////
	// end save state keys
	///////////////////////
	
	/** plays sound when an invalid tile is clicked */
	private transient MediaPlayer wrong;

	/**
	 * Paint object, store here to keep from having to re-initialize every
	 * redraw.
	 */
	transient Paint paint;

	/**
	 * Picture to use for the puzzle.
	 */
	transient Bitmap picture;

	/**
	 * List of tiles in the puzzle.
	 */
	private transient List<Tile> tiles;

	/**
	 * Width of the puzzle, in tiles
	 */
	private int width;

	/**
	 * Height of the puzzle, in tiles
	 */
	private int height;

	/**
	 * Number of moves to make when shuffling
	 */
	private int shuffleNum;

	/**
	 * Reference to the blank tile.
	 */
	private transient Tile blank;

	/**
	 * Width of each tile, in pixels
	 */
	transient int tileWidth;

	/**
	 * Height of each tile, in pixels
	 */
	transient int tileHeight;

	/**
	 * Whether or not the game has been won.
	 */
	private boolean won;
	
	/**
	 * Whether or not the game has been started
	 */
	private boolean started;
	
	/**
	 * Create a new SliderView and initialize to defaults.
	 * @param context Context of this View, to give access to Resources
	 * @param attrs container for various properties.
	 */
	public SliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SliderView(Context context) {
		super(context); 
		init();
	}
	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
		init();
	}

	/**
	 * Initializes all needed variables
	 */
	private void init() {
		// set size to default and initialize tiles
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		tiles = new ArrayList<Tile>(width * height);
		paint = new Paint();
		paint.setStrokeWidth(2);
		picture = BitmapFactory.decodeResource(getResources(), DEFAULT_PIC_ID);
		shuffleNum = DEFAULT_SHUFFLE_ITERATIONS;
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;

		won = false;
		started = false;
		
		Log.i("SliderView()", "adding myself as touchlistener");
	}
	 
	/**
	 * Do all setup work for a game, including initializing and shuffling Tiles
	 */
	public void startGame() {
		if (picture == null) {
			return;
		}

		// there could have been a previous game, get rid of game-specific 
		// state
		tiles.clear();
		
		Log.i("SliderView:startGame()", "tileWidth, tileHeight:(" + width +
				", " + height + ")");
		
		// initialize tiles
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// initialize current tile
				tiles.add(new Tile(i, j));
				Log.i("SliderView:startGame()", "added Tile...");
			}
		}

		// choose a random blank tile
		int blankIndex = (int)(Math.random() * tiles.size());
		blank = tiles.get(blankIndex);
		blank.setBlank(true);
		
		// determine tile sizes
		measureTiles();
		
		// randomize tiles, if the randomization happens to be correct, do it 
		// again
		do {
			won = false;
			randomize();
			checkWin();
		} while(won);
		
		// draw new stuff to screen
		invalidate();
	}

	/**
	 * Shuffles the tiles by picking a random tile to be blank and generating
	 * random moves.
	 */
	private void randomize() {
		// direction of the current move (horizontal or vertical)
		int direction;

		// perform shuffleNum moves at random
		for (int i = 0; i < shuffleNum; i ++) {
			Log.i("SliderView:randomize()", "shuffle: " + i);
			// decide if we're moving horizontal or vertical
			direction = (int)(Math.random() * 2);

			// swap horizontal or vertical
			if (direction == /*vertical*/0) {
				Log.i("SliderView:randomize(", "vertical");
				// swap up or down...
				int vDirection = (int)(Math.random() * 2);
				if (vDirection == /*up*/0 && blank.getDstY() != 0) {
					swapTileHelper(blank.getDstX(), blank.getDstY() - 1);
				} else if (/*down*/blank.getDstY() != height - 1) {
					swapTileHelper(blank.getDstX(), blank.getDstY() + 1);
				} else {
					// couldn't swap, repeat iteration
					i--;
				}
				// swap horizontal
			} else {
				Log.i("SliderView:randomize(", "horizontal");
				// swap left or right
				int hDirection = (int)(Math.random() * 2);
				if (hDirection == /*left*/0 && blank.getDstX() != 0) {
					swapTileHelper(blank.getDstX() - 1, blank.getDstY());
				} else if (/*down*/blank.getDstX() != width - 1) {
					swapTileHelper(blank.getDstX() + 1, blank.getDstY());
				} else {
					// couldn't swap, repeat iteration
					i--;
				}
			}
		}
	}
	
	/**
	 * Changes the shuffleNum value, given the parameter num
	 * @param num	Number to change shuffleNum to.
	 */
	public void changeShuffleNum(int num) {
		shuffleNum = num;
	}

	/**
	 * Iterate through all tiles and check to see if they are in their correct
	 * positions.  If they are, the game is won.
	 */
	private void checkWin() {
		if (tiles.size() != 0) {
			won = true;
			for (Tile t : tiles) {
				won = won && t.correct();
			}
		}
	}

	/**
	 * Change the size of the slider puzzle by giving changing the number of 
	 * tiles.
	 * 
	 * @param width new width in tiles
	 * @param height new height in tiles
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new ArrayList<Tile>(width * height);
		startGame();
	}
	
	/**
	 * Change the picture for the puzzle, automatically starts a new game.
	 * @param picture the new picture
	 */
	public void setPicture(Bitmap picture) {
		this.picture = Bitmap.createScaledBitmap(picture, getWidth(), 
												     getHeight(), true);
		startGame();
	}
	/**
	 * Helper method to swap a Tile given its coordinates in tiles instead
	 * of pixels.  Replicates a user click because it will swap with the blank
	 * tile.
	 * 
	 * @param tileX the X coodrinate of the Tile, measured in Tiles.
	 * @param tileY the Y coordinate of the Tile, measured in Tiles.
	 */
	private void swapTileHelper(int tileX, int tileY) {
		// find the tile to swap...
		int swapIndex = tiles.indexOf(new Tile(tileX, tileY));
		Tile swap = tiles.get(swapIndex);

		//...and swap it with the blank tile
		blank.swap(swap);
		blank.setBlank(false);
		swap.setBlank(true);
		blank = swap;
	}

	/**
	 * Represents a user click.  Find the tile at the given coordinates
	 * (in pixels) and, if it is adjacent to the blank tile, swap it and the 
	 * blank tile.
	 * 
	 * @param clickX the X coordinate of the click, in pixels
	 * @param clickY the Y coordinate of the click, in pixels
	 * @return true if the swap is legal, false otherwise
	 */
	public boolean swapTile(float clickX, float clickY) {
		if (!won) {
			// determine which tile was clicked
			int tileX = (int)(clickX / (tileWidth));
			int tileY = (int)(clickY / (tileHeight));

			// determine if the clicked tile borders the blank tile.
			// is adjacent if the clicked tile and blank tile have same Y
			// coordinate and adjacent X coordinates or vicaversa
			if (((Math.abs(tileX - blank.getDstX()) == 1) && 
							(tileY == blank.getDstY())) ||
							((Math.abs(tileY - blank.getDstY()) == 1) && 
						    (tileX == blank.getDstX()))) {
				Tile swap = tiles.get(
						tiles.indexOf(
								new Tile(tileX, tileY)));
				swap.swap(blank);
				blank.setBlank(false);
				swap.setBlank(true);
				blank = swap;
				invalidate();
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Called to change the tile width and heights due to orientation changes, 
	 * needed for when there is roundoff errors.
	 */
	private void measureTiles() {
		// figure out the size of each tile
		tileWidth = getWidth() / width;
		tileHeight = getHeight() / height;
		// if there is a rounding error form above, account for it
		tileWidth = getWidth() % width == 0 ? tileWidth : tileWidth + 1;
		tileHeight = getHeight() % height == 0 ? tileHeight : tileHeight + 1;
		
		
		// update tile sizes
		for (Tile t: tiles) {
			t.setTileHeight(tileHeight);
			t.setTileWidth(tileWidth);
		}
	}
	
	/**
	 * Called when the size of this view changes.  Initialize or change any
	 * size-dependent state.
	 */
	@Override
	protected synchronized void onSizeChanged(int width, int height, int oldW, int oldH) {
		Log.i("size", "start");
		// inflate picture from resources and then scale it to screen
		picture = Bitmap.createScaledBitmap(picture, width, height, true);
		measureTiles();
		
		Log.i("size:", "tileWidth: " + tileWidth + " tileHeight:" + tileHeight);
		if (!started) {
			startGame();
		}
		Log.i("size", "end");

	}


	/**
	 * Iterate through tiles to draw all the tiles.
	 * 
	 * @param canvas surface to draw on.
	 */
	@Override
	public void onDraw(Canvas canvas) {
		checkWin();
		

		if (!won) {
			// draw tiles
			for (Tile t : tiles) {
				t.draw(canvas, picture);
			}
			//
			// draw tile borders
			//
			int widthS = getWidth();    // width of the screen
			int heightS = getHeight();  // height of the screen			
			
			// draw outside edges	
			canvas.drawLine(0, 0, 0, getWidth(), paint);  // top
			canvas.drawLine(heightS, heightS, widthS, heightS, paint); // bottom
			canvas.drawLine(0, 0, 0, heightS, paint);  // left
			canvas.drawLine(widthS, widthS, widthS, heightS, paint); // right
			
			// draw horizontal inner lines
			for (int i = 1; i < height; i++) {
				canvas.drawLine(0, tileHeight * i, widthS,  
									tileHeight * i, paint);
			}
			
			// draw vertical inner lines
			for (int i = 1; i < width; i++) {
				canvas.drawLine(tileWidth * i, 0, tileWidth * i,  
												 heightS, paint);
			}
		} else {
			// draw the picture without tiles
			canvas.drawBitmap(picture, /*left*/0, /*top*/0, paint);
		}
	}
	
	/**
	 * onRestoreInstanceState is called whenever we are maintaining persistence
	 * through application change, or whenever our activity is put into the
	 * background, we can call this to restore its previous state after it is 
	 * saved.
	 * 
	 * @param state		A Parcelable object that holds variables that represent
	 * 					the state of the View, called to restore these held 
	 * 					state variables.
	 */
	@Override
	protected void onRestoreInstanceState (Parcelable state) {
		Log.i("restore()", "start");
		started = true;
		Bundle savedInstanceState = (Bundle)state;
		super.onRestoreInstanceState(savedInstanceState.getParcelable("super"));
		width = savedInstanceState.getInt(WIDTH_KEY);
		height = savedInstanceState.getInt(HEIGHT_KEY);
		Log.i("restore:width", "width:" + width);
		picture = savedInstanceState.getParcelable(BITMAP_KEY);
		won = savedInstanceState.getBoolean(WON_KEY);
		tiles = (ArrayList<Tile>) savedInstanceState.getSerializable(TILES_KEY);
		blank = (Tile)savedInstanceState.getSerializable(BLANK_KEY);
		Log.i("restore()", "end");

	}
	
	/**
	 * onSaveInstanceState saves the variables that represent the state 
	 * of the view, for persistence, and for use by onRestoreInstanceState.
	 */
	@Override
	protected Parcelable onSaveInstanceState () {
		Bundle savedInstanceState = new Bundle();
		savedInstanceState.putParcelable("super", super.onSaveInstanceState());
		savedInstanceState.putInt(WIDTH_KEY, width);
		savedInstanceState.putInt(HEIGHT_KEY, height);
		savedInstanceState.putParcelable(BITMAP_KEY, picture);
		savedInstanceState.putBoolean(WON_KEY, won);
		savedInstanceState.putSerializable(TILES_KEY, (ArrayList<Tile>)tiles);
		savedInstanceState.putSerializable(BLANK_KEY, blank);
		
		return savedInstanceState;
	}

	/**
	 * onTouchEvent is called whenever a touch occurs on the touch screen.
	 * 
	 * @param event		A MotionEvent object that represents the touch event.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("SliderView:onTouch()", "Method entered");
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.i("SliderView:onTouchEvent", "ACTION_DOWN");
			if (!swapTile(event.getX(), event.getY())) {
				Log.i("SliderActivity:onTouch()", "wrong tile...");
				try {
					playWrongSound();
				} catch (IOException ex) {
					Log.e(SliderActivity.class.getName(), "Failed to play sound.", ex);
				}
			}
			else {
				Log.i("SliderView:onTouch()", "good");
			}
			return true;
		}
		return false;
	}

	/**
	 * playWrongSound is called whenever a user touches the wrong set of tiles
	 * on the slider puzzle board, ex: an unmoveable tile.
	 * @throws IOException
	 */
    private void playWrongSound() throws IOException{
    	killMediaPlayer();
    	wrong = MediaPlayer.create(getContext(), R.raw.wrong);
    	wrong.start();
    }
    
    /**
     * killMediaPlayer() is called to kill a previous media player sound.
     */
    private void killMediaPlayer() {
    	if (wrong != null) {
    		try {
    			wrong.release();
    		} catch (Exception ex) {
    			Log.e(SliderActivity.class.getName(), 
    				  "Failed to kill the media player.", ex);
    		}
    	}
    }
}