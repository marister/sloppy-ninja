package tv.ouya.examples.android.sloppyninja;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import tv.ouya.console.api.OuyaController;


public class Player {
    public static Player instance;
    Camera mCamera;
    float axisX = 0;
    float axisY = 0;
    int mSpeed;
    boolean mIsSafe;
    
    Point mPlayerPosition;
    Point mImageOffset;
    
    public static enum Moves{
    	RIGHT,
    	LEFT,
    	UP,
    	DOWN
    }
    
    private SloppyNinjaActivity activity;
    
	private BitmapTextureAtlas mPlayerTextureAtlas;
	private TextureRegion mPlayerTexture;
	public AnimatedSprite playerImage;
	
	private BitmapTextureAtlas mPlayerRunTextureAtlas;
	private TiledTextureRegion  mPlayerRunTextureReg;
	//public Sprite playerRunImage;
	private int RUN_COLUMN = 8;
	private int RUN_ROWS = 1;
	private boolean mIsSlice;
	
	private boolean mIsLeftRight;
	private boolean mIsChangeDirection;
	
	static final private float c_playerRadius = 0.5f;
 
    public Player() {
    	 
    	mCamera = SloppyNinjaActivity.getSharedInstance().mCamera;
    	this.activity = SloppyNinjaActivity.getSharedInstance();
    	
        this.mPlayerRunTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,600,100, TextureOptions.BILINEAR);
        this.mPlayerRunTextureReg = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPlayerRunTextureAtlas, activity.getAssets(), "gfx/ninja_run.png", 0, 0, RUN_COLUMN, RUN_ROWS);
        this.mPlayerRunTextureAtlas.load();
        //activity.getEngine().getTextureManager().loadTexture(this.mPlayerTextureAtlas);
        this.playerImage = new AnimatedSprite(0, 0, this.mPlayerRunTextureReg, activity.getVertexBufferObjectManager());
        playerImage.animate(100);
        playerImage.setScale(0.8f, 0.8f);
        playerImage.setPosition(mCamera.getWidth() / 2 - playerImage.getWidth() / 2,
            mCamera.getHeight()/2 - playerImage.getHeight()/2 );
        playerImage.setZIndex(10);
        initPlayer();
    }
    
    private void initPlayer()
    {
    	mSpeed = 6;
    	mPlayerPosition = new Point(100,100);
    	mImageOffset = new Point(-40,-60);
    	playerImage.setPosition(mPlayerPosition.x + mImageOffset.x,mPlayerPosition.y + mImageOffset.y);
    	mIsSafe = true;
    	mIsSlice = false;
    	mIsLeftRight = false;
    	mIsChangeDirection = false;
    }

	public void move(Moves nextMove) {
		int speedX = 0;
		int speedY = 0;
		
		
		switch(nextMove)
		{
		case RIGHT:
			speedX = mSpeed;
			//playerImage.setScaleX(1f);
			playerImage.setFlippedHorizontal(false);
			break;
		case LEFT:
			//playerImage.setScaleX(-1f);
			playerImage.setFlippedHorizontal(true);
			speedX = -mSpeed;
			break;
		case UP:
			speedY = -mSpeed;
			break;
		case DOWN:
			speedY = mSpeed;
			break;
		}
		
		float nextPositionX = mPlayerPosition.x+speedX;
		float nextPositionY = mPlayerPosition.y+speedY;
		
		if (mIsSafe == true)
		{
			safefLogic((int)nextPositionX,(int)nextPositionY);
		}
		else
		{
			dangerLogic((int)nextPositionX,(int)nextPositionY);
		}
		//playerImage.setPosition(playerImage.getX()+speedX,playerImage.getY()+speedY);
	}
	
	private void safefLogic(int nextPositionX, int nextPositionY) {
		//Log.i("Player","safe  "+nextPositionX+"   "+nextPositionY);
		//Log.i("Player",""+BoardManager.GetPixelColor(nextPositionX, nextPositionY)+"   "+BoardManager.LineColor.WHITE);
		if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.BORDER)
		{
			ChangePlayerPosition(nextPositionX, nextPositionY);
		}
		else
			if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.VOID)
			{
				//playerImage.setPosition(nextPositionX,nextPositionY);
				findNextBorderPosition(nextPositionX,nextPositionY);
			}
			else
				if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.EMPTY
						&& mIsSlice == true)
				{
					//playerImage.setPosition(nextPositionX,nextPositionY);
					startSlice(nextPositionX,nextPositionY);
				}
	}
	
	private void startSlice(int nextPositionX, int nextPositionY) {
		mIsSafe = false;
		Point curPosition = new Point(mPlayerPosition.x, mPlayerPosition.y);
		ChangePlayerPosition(nextPositionX, nextPositionY);
		((GameScene)activity.mCurrentScene).mBoardManager.DrawLine(curPosition.x, curPosition.y, nextPositionX, nextPositionY, BoardManager.BoardColor.REDLINE, true);
		
	}
	
	private void dangerLogic(int nextPositionX, int nextPositionY) {
		Point curPosition = new Point(mPlayerPosition.x, mPlayerPosition.y);
		if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.BORDER)
		{
			ChangePlayerPosition(nextPositionX, nextPositionY);
			closeArea(curPosition, nextPositionX, nextPositionY);
		}
		else
			if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.VOID)
			{
				findNextBorderPosition(nextPositionX,nextPositionY);
				closeArea(curPosition, nextPositionX, nextPositionY);
			}
			else
				if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.EMPTY)
				{
					ChangePlayerPosition(nextPositionX, nextPositionY);
					((GameScene)activity.mCurrentScene).mBoardManager.DrawLine(curPosition.x, curPosition.y, nextPositionX, nextPositionY, BoardManager.BoardColor.REDLINE, mIsChangeDirection);
				}
	}

	private void closeArea(Point curPosition, int nextPositionX, int nextPositionY) {
		System.out.println("CLOSE AREA");
		((GameScene)activity.mCurrentScene).mBoardManager.DrawLine(curPosition.x, curPosition.y, nextPositionX, nextPositionY, BoardManager.BoardColor.REDLINE, mIsChangeDirection);
		((GameScene)activity.mCurrentScene).mBoardManager.ClearPivots();
		mIsSafe = true;
		mIsSlice = false;
		mIsLeftRight = false;
    	mIsChangeDirection = false;
	}

	private void findNextBorderPosition(int nextPositionX, int nextPositionY) {
		Point maxPixelsToMove = new Point(Math.abs(mPlayerPosition.x - nextPositionX)
											,Math.abs(mPlayerPosition.y - nextPositionY) );
		int pixelCounter = 0;
		boolean foundBorder = false;
		int i_x = 0;
		while (i_x < maxPixelsToMove.x && foundBorder == false)
		{
			int positiveFactor = Math.abs(mPlayerPosition.x - nextPositionX) / (mPlayerPosition.x - nextPositionX);
			int curPosToCheckX = mPlayerPosition.x - (mPlayerPosition.x - nextPositionX) + (i_x * positiveFactor);
			if (BoardManager.GetPixelColor(curPosToCheckX, mPlayerPosition.y) == BoardManager.BoardColor.BORDER)
			{
				ChangePlayerPosition(curPosToCheckX, mPlayerPosition.y);
				foundBorder = true;
			}
			i_x = i_x + 1;
		}
		
		foundBorder = false;
		int i_y = 0;
		while (i_y < maxPixelsToMove.y && foundBorder == false)
		{
			int positiveFactor = Math.abs(mPlayerPosition.y - nextPositionY) / (mPlayerPosition.y - nextPositionY);
			int curPosToCheckY = mPlayerPosition.y - (mPlayerPosition.y - nextPositionY) + (i_y * positiveFactor);
			
			if (BoardManager.GetPixelColor(mPlayerPosition.x, curPosToCheckY) == BoardManager.BoardColor.BORDER)
			{
				ChangePlayerPosition(mPlayerPosition.x, curPosToCheckY);
				foundBorder = true;
			}
			i_y = i_y + 1;
		}
	}

	private void ChangePlayerPosition(int nextPositionX, int nextPositionY) {
		if (nextPositionX - mPlayerPosition.x != 0)
		{
			if (mIsLeftRight == false)
			{
				mIsChangeDirection = true;
				
			}
			else
			{
				mIsChangeDirection = false;
			}
			mIsLeftRight = true;
		}
		else
			if (nextPositionY - mPlayerPosition.y != 0)
			{
				if (mIsLeftRight == true)
				{
					mIsChangeDirection = true;
				}
				else
				{
					mIsChangeDirection = false;
				}
				mIsLeftRight = false;
			}
		mPlayerPosition.x = nextPositionX;
		mPlayerPosition.y = nextPositionY;
		playerImage.setPosition(mPlayerPosition.x + mImageOffset.x,mPlayerPosition.y + mImageOffset.y);
	}
	
	static private float stickMag(float axisX, float axisY) {
	    float stickMag = (float) Math.sqrt(axisX * axisX + axisY * axisY);
	    return stickMag;
	}
	
	static public boolean isStickNotCentered(float axisX, float axisY) {
        float stickMag = stickMag(axisX, axisY);
        return (stickMag >= OuyaController.STICK_DEADZONE);
    }
	
	protected void update(){
		
		checkStickDirection();
		//Log.i("player","x: "+playerImage.getX()+"  y: "+playerImage.getY());
	}

	private void checkStickDirection() {
		if (axisX >= -1f && axisX < -0.5f && axisY >= -0.75f && axisY < 0.75f){
			move(Moves.LEFT);
		}
		else
			if (axisX >= -0.5f && axisX < 0.5f && axisY <= -0.75f){
				move(Moves.UP);
			}
			else
				if (axisX >= -0.5f && axisX < 0.5f && axisY >= 0.75f){
					move(Moves.DOWN);
				}
				else
					if (axisX >= 0.5f  && axisY > -0.75f && axisY < 0.75f){
						move(Moves.RIGHT);
					}
	}

	public void updateAxis(float x, float y) {
		axisX = x;
		axisY = y;
		
	}

	public void ActionSlice_down() {
		if (mIsSafe == true)
		{
			mIsSlice = true;
		}
		
	}
	
	public void ActionSlice_up() {
		if (mIsSafe == true)
		{
			mIsSlice = false;
		}
		
	}
}



