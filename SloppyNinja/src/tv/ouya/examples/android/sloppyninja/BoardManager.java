package tv.ouya.examples.android.sloppyninja;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.opengl.GLES20;
import android.util.Log;

public class BoardManager {
	private SloppyNinjaActivity activity;
	BitmapTextureAtlas mHiddenBoardBitmapTextureAtlas;
	private BitmapTextureAtlasSource mBoardBitmapSource;
	private Sprite mBoardSprite;
	private GameScene mGameScene;
	TextureRegion mBoardRegion;
//	private int[] mColorsArray;
	private static Bitmap mHiddenBitmap;
	ArrayList<Point> mPivotList;
	ArrayList<Line> mLinesList;
	
	public BoardManager(GameScene i_gameScene)
	{
		this.activity = SloppyNinjaActivity.getSharedInstance();
		this.mGameScene = i_gameScene;
		this.mHiddenBoardBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		showHiddenBitmap();
		
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
		
		mPivotList = new ArrayList<Point>();
		mLinesList = new ArrayList<Line>();
		//initBoard();
	}
	
	private void initBoard() {
		
	}

	public static int GetPixelColor(int x, int y){
		return mHiddenBitmap.getPixel(x, y);
		//Log.i("BoardManager","value = "+value + "   pixel = "+mHiddenBitmap.getPixel(100, 100));
		//return value;
	}
	

	private void showHiddenBitmap(){
		this.mBoardRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mHiddenBoardBitmapTextureAtlas, activity, "gfx/Board_blank.png", 0, 0);
		this.activity.getEngine().getTextureManager().loadTexture(mHiddenBoardBitmapTextureAtlas);
		this.mBoardSprite = new Sprite(0, 0, mBoardRegion, activity.getVertexBufferObjectManager());
		this.mBoardSprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		mBoardSprite.setZIndex(0);
		
		this.mGameScene.attachChild(mBoardSprite);
		this.mGameScene.sortChildren();
	}
	
	private void updateHiddenBitmap()
	{
		mBoardBitmapSource = new BitmapTextureAtlasSource(mHiddenBitmap);
		
		this.mHiddenBoardBitmapTextureAtlas.clearTextureAtlasSources();
		
		this.mHiddenBoardBitmapTextureAtlas.addTextureAtlasSource(mBoardBitmapSource, 0, 0);
		this.mHiddenBoardBitmapTextureAtlas.load();
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
	
	public void DrawLine(int i_x1, int i_y1, int i_x2,	int i_y2, int i_color, boolean isPivot) {
		clearRedLines();
		if (isPivot == true)
		{
			mPivotList.add(new Point(i_x1, i_y1));
		}
		drawRedLines(i_x2, i_y2);
	}

	private void drawRedLines(int i_x, int i_y) {
		mLinesList.clear();
		System.out.println(mPivotList.size());
		for (int i = 1; i < mPivotList.size(); i++)
		{
			mLinesList.add(new Line(mPivotList.get(i-1).x, mPivotList.get(i-1).y, mPivotList.get(i).x, mPivotList.get(i).y,activity.getVertexBufferObjectManager()) );
			Line curLine = mLinesList.get(mLinesList.size() - 1);
			curLine.setColor(255, 255, 255, 255);
			curLine.setZIndex(9);
			this.mGameScene.attachChild(curLine);
			this.mGameScene.sortChildren();
		}
		if (mPivotList.size() > 0)
		{
			mLinesList.add(new Line(mPivotList.get(mPivotList.size() - 1).x, mPivotList.get(mPivotList.size() - 1).y, i_x, i_y,activity.getVertexBufferObjectManager()) );
			Line curLine = mLinesList.get(mLinesList.size() - 1);
			curLine.setColor(255, 255, 255, 255);
			curLine.setZIndex(9);
			this.mGameScene.attachChild(curLine);
			this.mGameScene.sortChildren();
		}
	}

	private void clearRedLines() {
		for (int i = 0; i < mLinesList.size(); i++)
		{
			this.mGameScene.detachChild(mLinesList.get(i));
		}
		mLinesList.clear();
	}
	
	public void ClearPivots()
	{
		mPivotList.clear();
	}
	
	public void FillArea()
	{
		final Point p1 = new Point();
		p1.x=(int) 950; 
		p1.y=(int) 200; 
		
		final FloodFill f= new FloodFill();

        //ib =  BitmapFactory.decodeStream(in, null, decodeOptions);
  
        //Bitmap.Config config = ib.getConfig() ;
        final Bitmap ib = mBoardBitmapSource.getBitmap();
		
        //final Scene thisScene = this;
        //final long randColor = 2000000000l + (long)(Math.random() * ((4279308560l - 2000000000l) + 1));
		//final String destColor = Long.toHexString(randColor);
        //int randColor = 100000000;
        final String destColor;// = "ff35FFD6";//Integer.toHexString(randColor);
       
		int pixelColor = ib.getPixel(p1.x, p1.y);
		final String sourceColor = Integer.toHexString(pixelColor);
		final int randColor;
		if (pixelColor != 0)
		{
			destColor = "0";
			long randColorL = Long.valueOf(destColor, 16);
		    randColor = (int)randColorL;//Long.valueOf(destColor, 16);
		   // Log.i("thread","here1a  "+pixelColor+"   "+sourceColor+"  "+randColor+"   "+destColor);
		}
		else
		{
			destColor = "ff35FFD6";
			long randColorL = Long.valueOf(destColor, 16);
		    randColor = (int)randColorL;//Long.valueOf(destColor, 16);
		   // Log.i("thread","here1b  "+pixelColor+"   "+sourceColor+"  "+randColor+"   "+destColor);
		}
		
        
        Thread T = new Thread(new Runnable(){
			
			@Override
			public void run() {
				
				f.floodFill(ib,p1,sourceColor,randColor);
				
				//BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(ib);
				mBoardBitmapSource = new BitmapTextureAtlasSource(ib);
				
				mHiddenBoardBitmapTextureAtlas.clearTextureAtlasSources();
				mHiddenBoardBitmapTextureAtlas.addTextureAtlasSource(mBoardBitmapSource, 0, 0);
				mHiddenBoardBitmapTextureAtlas.load();
				
				int pixelColor2 = ib.getPixel(p1.x, p1.y);
				String sourceColor2 = Integer.toHexString(pixelColor2);
				//Log.i("thread","here3  "+pixelColor2+"   "+sourceColor2);
				
			}
		});
        T.start();
	}
}
