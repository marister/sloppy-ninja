package tv.ouya.examples.android.sloppyninja;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;

import tv.ouya.console.api.OuyaController;
import tv.ouya.examples.android.sloppyninja.SloppyNinjaActivity.SceneIndex;

public class SplashScreen extends Scene{
	private SloppyNinjaActivity activity;
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private TextureRegion mBackgroundTexture;
	private Sprite mBackground;
	
	public SplashScreen(){
		activity = SloppyNinjaActivity.getSharedInstance();
		
        this.mBackgroundTextureAtlas = new BitmapTextureAtlas(activity.getEngine().getTextureManager()
        											,1280,720, TextureOptions.BILINEAR);
        this.mBackgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBackgroundTextureAtlas, activity, "gfx/bg.jpg", 0, 0);
        activity.getEngine().getTextureManager().loadTexture(this.mBackgroundTextureAtlas);
		this.mBackground = new Sprite(0, 0, this.mBackgroundTexture, activity.getVertexBufferObjectManager());
		
		this.attachChild(this.mBackground);
	}
	
	public void setRegisterEntityModifier(){
	    DelayModifier dMod = new DelayModifier(0.2f){
	        @Override
	        protected void onModifierFinished(IEntity pItem) {
	        	activity.setCurrentScene(SceneIndex.MENU);
	        }
	    };
	    
	    activity.mCurrentScene.registerEntityModifier(dMod);
	}

	public void onKeyUp(int keyCode){
	
	}
	
	public void onKeyDown(int keyCode){
		
	}		
}
