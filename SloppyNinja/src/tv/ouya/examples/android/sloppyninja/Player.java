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
        
        initPlayer();
    }
    
    private void initPlayer()
    {
    	mSpeed = 6;
    	mPlayerPosition = new Point(100,100);
    	mImageOffset = new Point(-30,-60);
    	playerImage.setPosition(mPlayerPosition.x + mImageOffset.x,mPlayerPosition.y + mImageOffset.y);
    	mIsSafe = true;
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
			mPlayerPosition.x = nextPositionX;
			mPlayerPosition.y = nextPositionY;
			playerImage.setPosition(mPlayerPosition.x + mImageOffset.x,mPlayerPosition.y + mImageOffset.y);
		}
		else
			if (BoardManager.GetPixelColor(nextPositionX, nextPositionY) == BoardManager.BoardColor.EMPTY)
			{
				//playerImage.setPosition(nextPositionX,nextPositionY);
				mPlayerPosition.x = nextPositionX;
				mPlayerPosition.y = nextPositionY;
				playerImage.setPosition(mPlayerPosition.x + mImageOffset.x,mPlayerPosition.y + mImageOffset.y);
			}
	}
	
	private void dangerLogic(int nextPositionX, int nextPositionY) {
		// TODO Auto-generated method stub
		
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
		Log.i("player","x: "+playerImage.getX()+"  y: "+playerImage.getY());
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
}



