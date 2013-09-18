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
	BitmapTextureAtlas mLevelBitmapTextureAtlas;
	BitmapTextureAtlas mBoardBitmapTextureAtlas;
	BitmapTextureAtlasSource mBoardBitmapSource;
	TextureRegion mBackgroundRegion;
	Sprite mBackgroundSprite;
	public BoardManager mBoardManager;
	
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
		
		mBoardManager = new BoardManager(this);
	}
	
	private void createBitmapData() {
		mLevelBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mLevelBitmapTextureAtlas, activity, "gfx/level1.jpg", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mLevelBitmapTextureAtlas);
		mBackgroundSprite = new Sprite(0, 0, mBackgroundRegion, activity.getVertexBufferObjectManager());
		
		
		this.attachChild(mBackgroundSprite);
		
		/*mBoardBitmapTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
				,1280,720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoardRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBoardBitmapTextureAtlas, activity, "gfx/Board_blank.png", 0, 0);
		activity.getEngine().getTextureManager().loadTexture(mBoardBitmapTextureAtlas);
		mBoardSprite = new Sprite(0, 0, mBoardRegion, activity.getVertexBufferObjectManager());
		mBoardSprite.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.attachChild(mBoardSprite);
		*/
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
		
		
		
		
	}

	private void initPhysics() {
	    physicsWorld = new PhysicsWorld(new Vector2(), false);//new Vector2(0, SensorManager.GRAVITY_EARTH), false);
	    this.registerUpdateHandler(physicsWorld);
	    physicsWorld.setContactListener(createContactListener());
	}
	
	/*public void createLevelBackground(){
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
	} */

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
			//FillArea();
        }
		if (keyCode == OuyaController.BUTTON_O) {
			player.ActionSlice_down();
        }
	}

	public void onKeyUp(int keyCode) {
		// TODO Auto-generated method stub
		if (keyCode == OuyaController.BUTTON_O) {
			player.ActionSlice_up();
        }
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

	
}
