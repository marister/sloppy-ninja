package tv.ouya.examples.android.sloppyninja;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasSource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class BoardManager {
	private SloppyNinjaActivity activity;
	BitmapTextureAtlas mHiddenBoardBitmapTextureAtlas;
	private BitmapTextureAtlasSource mBoardBitmapSource;
//	private int[] mColorsArray;
	private static Bitmap mHiddenBitmap;
	
	/*
	static public enum LineColor{
		//WHITE(111111111), RED(222222222), EMPTY(333333333), VOID(0);
		WHITE(Color.argb(255, 255, 255, 255)), RED(Color.argb(255, 255, 0, 0)), EMPTY(Color.argb(255, 0, 0, 0)), VOID(0);
		
		private int val;
		
		LineColor(int val) {
	        this.val = val;
	    }

	    public int getVal() {
	        return this.val;
	    }
	    
	    public static LineColor getType(int val){
	    	LineColor answer = null;
	    	if (WHITE.getVal() == val)
	    	{
	    		answer = LineColor.WHITE;
	    	}
	    	else
	    		if (RED.getVal() == val)
		    	{
		    		answer = LineColor.RED;
		    	}
	    		else
		    		if (EMPTY.getVal() == val)
			    	{
			    		answer = LineColor.EMPTY;
			    	}
		    		else
			    		if (VOID.getVal() == val)
				    	{
				    		answer = LineColor.VOID;
				    	}
	    	
	    	return answer;
	    }
	}*/
	
	public BoardManager()
	{
		activity = SloppyNinjaActivity.getSharedInstance();
		mHiddenBoardBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		InputStream in = null;
		try {
			in = activity.getAssets().open("gfx/Board_blank.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;
		Bitmap tmpBitmap = BitmapFactory.decodeStream(in, null, decodeOptions);
		mBoardBitmapSource = new BitmapTextureAtlasSource(tmpBitmap);
		mHiddenBitmap = mBoardBitmapSource.getBitmap();
		
		BoardColor.InitBoard(mHiddenBitmap);
		//initBoard();
	}
	
	private void initBoard() {
		
	}

	/*private void initBoard() {
		Point startPosition = new Point(100,100);
		int width = 1080;
		int height = 520;
		
		//clear all bitmap pixels to void ( = 0 );
		for (int i = 0; i  < activity.CAMERA_WIDTH; i++)
		{
			for (int j = 0; j  < activity.CAMERA_HEIGHT; j++)
			{
				mHiddenBitmap.setPixel(i, j, LineColor.VOID.getVal());
			}
		}
		
		//set playable area pixels to empty (  );
		for (int i = 0; i  < width + 1; i++)
		{
			for (int j = 0; j  < height + 1; j++)
			{
				mHiddenBitmap.setPixel(startPosition.x + i, startPosition.y + j, LineColor.EMPTY.getVal());
			}
		}
		
		//set white line border pixels to WHITE (  );
		for (int i = 0; i  < width + 1; i++)
		{
			mHiddenBitmap.setPixel(startPosition.x + i, startPosition.y, LineColor.WHITE.getVal());
			mHiddenBitmap.setPixel(startPosition.x + i, startPosition.y + height, LineColor.WHITE.getVal());
			//System.out.println((startPosition.x + i)+"   "+startPosition.y+"   "+mHiddenBitmap.getPixel(startPosition.x + i, startPosition.y));
		}
		
		for (int j = 0; j  < height + 1; j++)
		{
			mHiddenBitmap.setPixel(startPosition.x , startPosition.y + j, LineColor.WHITE.getVal());
			mHiddenBitmap.setPixel(startPosition.x + width, startPosition.y + j, LineColor.WHITE.getVal());
		}
		
		//Log.i("BoardManager","init = "+mHiddenBitmap.getPixel(100, 100));
		//showHiddenBitmap();
	}*/
	
	public static int GetPixelColor(int x, int y){
		return mHiddenBitmap.getPixel(x, y);
		//Log.i("BoardManager","value = "+value + "   pixel = "+mHiddenBitmap.getPixel(100, 100));
		//return value;
	}
	

	private void showHiddenBitmap(){
		activity.getEngine().getTextureManager().loadTexture(mHiddenBoardBitmapTextureAtlas);
		mHiddenBoardBitmapTextureAtlas.clearTextureAtlasSources();
		mHiddenBoardBitmapTextureAtlas.addTextureAtlasSource(mBoardBitmapSource, 0, 0);
		mHiddenBoardBitmapTextureAtlas.load();
	}
	
	public static class BoardColor{
		public static int BORDER=0;
		public static int REDLINE=0;
		public static int EMPTY=0;
		public static int VOID=0;
		
		public static void InitBoard(Bitmap i_bitmap){
			BORDER = i_bitmap.getPixel(100, 100);
			EMPTY = i_bitmap.getPixel(101, 101);
			VOID = i_bitmap.getPixel(99, 99);
			REDLINE = Color.argb(255, 255, 0, 0);
		}
	}
}
