package tv.ouya.examples.android.sloppyninja;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasSource;

import javax.microedition.khronos.opengles.GL11;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.engine.handler.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import tv.ouya.console.api.OuyaController;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

public class GameScene extends Scene{
	private Player player;
	private SloppyNinjaActivity activity;
	private PhysicsWorld physicsWorld;
	
	BitmapTextureAtlas mBoardAtlas;
	TextureRegion mBoardRegion;
	Sprite  mBoardSprite;
	Bitmap b;
	BitmapTextureAtlas mLevelBitmapTextureAtlas;
	BitmapTextureAtlas mBoardBitmapTextureAtlas;
	BitmapTextureAtlasSource mBoardBitmapSource;
	TextureRegion mBackgroundRegion;
	Sprite mBackgroundSprite;
	
	public GameScene(){
		activity = SloppyNinjaActivity.getSharedInstance();
		
		//createLevelBackground();
		//createLevelBoard();
		createBitmapData();
		
		player = new Player();
		
		player.playerImage.setUserData("player");
		player.playerImage.registerUpdateHandler(new IUpdateHandler(){
							@Override
							    public void reset() {
							
							    }
							
							    @Override
							        public void onUpdate(float pSecondsElapsed) {
							    		player.update();
							        }
							});
		this.attachChild(player.playerImage);
	}
	
	private void createBitmapData() {
		// TODO Auto-generated method stub
		mLevelBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mLevelBitmapTextureAtlas, activity, "gfx/level1.jpg", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mLevelBitmapTextureAtlas);
		mBackgroundSprite = new Sprite(0, 0, mBackgroundRegion, activity.getVertexBufferObjectManager());
		
		
		this.attachChild(mBackgroundSprite);
		
		mBoardBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoardRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBoardBitmapTextureAtlas, activity, "gfx/Board_blank.png", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mBoardBitmapTextureAtlas);
		mBoardSprite = new Sprite(0, 0, mBoardRegion, activity.getVertexBufferObjectManager());
		mBoardSprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
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
		
		
		
		this.attachChild(mBoardSprite);
	}

	private void initPhysics() {
	    physicsWorld = new PhysicsWorld(new Vector2(), false);//new Vector2(0, SensorManager.GRAVITY_EARTH), false);
	    this.registerUpdateHandler(physicsWorld);
	    physicsWorld.setContactListener(createContactListener());
	}
	
	public void createLevelBackground(){
		BitmapTextureAtlas mBackgroundTextureAtlas;
		TextureRegion mBackgroundTexture;
		Sprite mBackground;
		
        mBackgroundTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR);
		mBackgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundTextureAtlas, activity, "gfx/level1.jpg", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mBackgroundTextureAtlas);
		mBackground = new Sprite(0, 0, mBackgroundTexture, activity.getVertexBufferObjectManager());
		
		this.attachChild(mBackground);
	}
	
	public void createLevelBoard(){
		
		mBoardAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR);
		mBoardRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBoardAtlas, activity, "gfx/Board_blank.png", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mBoardAtlas);
		mBoardSprite = new Sprite(0, 0, mBoardRegion, activity.getVertexBufferObjectManager());
		
		//b = BitmapFactory.decodeFile("gfx/Board_blank.png");
		//Bitmap bMap = BitmapFactory.decodeFile("gfx/Board_blank.png");
		//mBoardImage.setImageBitmap(bMap);
	//	Bitmap bitmap = BitmapFactory.decodeStream(activity.getEngine().getTextureManager().);
		//Drawable mDrawable;
		this.attachChild(mBoardSprite);
	}

	public void onKeyDown(int keyCode) {
		if (keyCode == OuyaController.BUTTON_DPAD_RIGHT){
			player.move(Player.Moves.RIGHT);
		}
		if (keyCode == OuyaController.BUTTON_DPAD_LEFT){
			player.move(Player.Moves.LEFT);
		}
		if (keyCode == OuyaController.BUTTON_DPAD_DOWN){
			player.move(Player.Moves.DOWN);
		}
		if (keyCode == OuyaController.BUTTON_DPAD_UP){
			player.move(Player.Moves.UP);
		}
		if (keyCode == OuyaController.BUTTON_U) {
			FillArea();
        }
	}

	public void onKeyUp(int keyCode) {
		// TODO Auto-generated method stub
		
	}

	public void updateAxis(float x, float y) {
		player.updateAxis(x,y);
	}
	
	private ContactListener createContactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        @Override
	        public void beginContact(Contact contact)
	        {
	            final Fixture x1 = contact.getFixtureA();
	            final Fixture x2 = contact.getFixtureB();
	        }

	        @Override
	        public void endContact(Contact contact)
	        {
	               
	        }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}

	    };
	    return contactListener;
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
		    Log.i("thread","here1a  "+pixelColor+"   "+sourceColor+"  "+randColor+"   "+destColor);
		}
		else
		{
			destColor = "ff35FFD6";
			long randColorL = Long.valueOf(destColor, 16);
		    randColor = (int)randColorL;//Long.valueOf(destColor, 16);
		    Log.i("thread","here1b  "+pixelColor+"   "+sourceColor+"  "+randColor+"   "+destColor);
		}
		
        
        Thread T = new Thread(new Runnable(){
			
			@Override
			public void run() {
				
				f.floodFill(ib,p1,sourceColor,randColor);
				
				//BitmapTextureAtlasSource source = new BitmapTextureAtlasSource(ib);
				mBoardBitmapSource = new BitmapTextureAtlasSource(ib);
				
				mBoardBitmapTextureAtlas.clearTextureAtlasSources();
				mBoardBitmapTextureAtlas.addTextureAtlasSource(mBoardBitmapSource, 0, 0);
				mBoardBitmapTextureAtlas.load();
				
				int pixelColor2 = ib.getPixel(p1.x, p1.y);
				String sourceColor2 = Integer.toHexString(pixelColor2);
				Log.i("thread","here3  "+pixelColor2+"   "+sourceColor2);
				
			}
		});
        T.start();
		
		
	}
}
