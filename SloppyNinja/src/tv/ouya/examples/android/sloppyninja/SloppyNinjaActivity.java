/*
 * Copyright (C) 2012, 2013 OUYA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tv.ouya.examples.android.sloppyninja;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;

import tv.ouya.console.api.OuyaController;
import tv.ouya.examples.android.sloppyninja.Player.Moves;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SloppyNinjaActivity extends BaseGameActivity {
	//for log
	private static final String TAG = "Game Activity";
	
	//ouya dimensions
	protected static final int CAMERA_WIDTH = 1280;
	protected static final int CAMERA_HEIGHT = 720;
	static public Boolean pauseInput = new Boolean(false);
	
	//scenes indexing
	static public enum SceneIndex{
		SPLASH,
		MENU,
		GAME
	}
	
	static public SceneIndex currentSceneIndex;
	
	//for menu
	public Font mFont;
	
	//main scene camera!
	public Camera mCamera;
	
	//A reference to the current scene
	public Scene mCurrentScene;
	private static SloppyNinjaActivity instance; //singleton game!
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }	
    
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.instance = this;
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		//initialize fonts for menu
		mFont = FontFactory.create(this.getFontManager(),this.getTextureManager(), 256, 256,Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
	    mFont.load();
	    
	    mEngine.registerUpdateHandler(new FPSLogger());
		OuyaController.init(this); //Initialize ouya controller class?
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
	    
		/** Auto 2 Seconds before switching to menu scene
		 * In real game this should be when finished loading */
		SloppyNinjaActivity.currentSceneIndex = SceneIndex.SPLASH;
		
		mCurrentScene = new SplashScreen();
		((SplashScreen)mCurrentScene).setRegisterEntityModifier();
		
		pOnCreateSceneCallback.onCreateSceneFinished(mCurrentScene);
	}
	
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}	
	
	public static SloppyNinjaActivity getSharedInstance() {
	    return instance;
	}
	 
	// to change the current main scene
	public void setCurrentScene(SceneIndex sceneIndex) {
		switch(sceneIndex){
			case SPLASH:
				SloppyNinjaActivity.currentSceneIndex = SceneIndex.SPLASH;
				mCurrentScene = new SplashScreen();
				((SplashScreen)mCurrentScene).setRegisterEntityModifier();
				break;
			case MENU:
				SloppyNinjaActivity.currentSceneIndex = SceneIndex.MENU;
				mCurrentScene = new MainMenuScene();
				break;
			case GAME:
				SloppyNinjaActivity.currentSceneIndex = SceneIndex.GAME;
				mCurrentScene = new GameScene();
				break;
		}
		//Log.i("asd","set scene?");
		getEngine().setScene(mCurrentScene);
	}
	/*************************** OUYA STUFF!!! *****************************/
	/***********************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//Log.i(TAG, "Key Down! " + keyCode);
    	if(SloppyNinjaActivity.currentSceneIndex == SceneIndex.MENU){
    		if (keyCode == OuyaController.BUTTON_A) {
                finish();
            }
    		else
    			{
    				((MainMenuScene)this.mCurrentScene).onKeyDown(keyCode);
    			}
    	}
    	else
		if(SloppyNinjaActivity.currentSceneIndex == SceneIndex.GAME){
			if (keyCode == OuyaController.BUTTON_A) {
				setCurrentScene(SceneIndex.MENU);
            }
			else
				if (keyCode == OuyaController.BUTTON_U) {
					((GameScene)this.mCurrentScene).onKeyDown(keyCode);
	            }
	    		else
	    			{
	    				((GameScene)this.mCurrentScene).onKeyDown(keyCode);
	    			}
    	}
    	
    	return true;
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	//Log.i(TAG, "Key Up! " + keyCode+"    "+OuyaController.AXIS_R2+" "+OuyaController.AXIS_RS_X+" "+OuyaController.AXIS_RS_Y);
    	if(SloppyNinjaActivity.currentSceneIndex == SceneIndex.MENU){
    		((MainMenuScene)this.mCurrentScene).onKeyUp(keyCode);
    	}
    	else
		if(SloppyNinjaActivity.currentSceneIndex == SceneIndex.GAME){
    		((GameScene)this.mCurrentScene).onKeyUp(keyCode);
    	}
    	return true;
    	
//        if(keyCode == OuyaController.BUTTON_L3) {
//            return true;
//        } else if (keyCode == OuyaController.BUTTON_R3) {
//            return true;
//        } else if (findViewById(keyCode) != null && keyCode != OuyaController.BUTTON_L2 && keyCode != OuyaController.BUTTON_R2) {
//            return true;
//        } else {
//            return super.onKeyUp(keyCode, event);
//        }
    }
    
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
    	boolean handled = false;
        synchronized (pauseInput) {
            handled = OuyaController.onGenericMotionEvent(event);
        }
        float x = event.getAxisValue(OuyaController.AXIS_LS_X);
    	float y = event.getAxisValue(OuyaController.AXIS_LS_Y);
    	
    	if(SloppyNinjaActivity.currentSceneIndex == SceneIndex.GAME){
    		((GameScene)this.mCurrentScene).updateAxis(x,y);
    	}
    	//Log.i("asd",x+"  "+y);
        return handled || super.onGenericMotionEvent(event);
    	
    } 
   

}