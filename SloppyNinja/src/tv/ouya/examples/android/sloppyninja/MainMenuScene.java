package tv.ouya.examples.android.sloppyninja;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.util.color.Color;

import tv.ouya.console.api.OuyaController;
import tv.ouya.examples.android.sloppyninja.R;

import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;

public class MainMenuScene extends MenuScene{
	
	final int MENU_START = 0;
	
	private SloppyNinjaActivity activity;
	private Font mFont;
	private IMenuItem startButton;
	private IMenuItem quitButton;
	
	public MainMenuScene(){
		super(SloppyNinjaActivity.getSharedInstance().mCamera);
		
		this.activity = SloppyNinjaActivity.getSharedInstance();
		 
		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.startButton = new TextMenuItem(MENU_START, activity.mFont, activity.getString(R.string.new_game), activity.getVertexBufferObjectManager());
		startButton.setPosition(mCamera.getWidth() / 2 - startButton.getWidth() / 2, mCamera.getHeight() / 2 - startButton.getHeight() / 2);
		addMenuItem(startButton);
		
		this.quitButton = new TextMenuItem(MENU_START, activity.mFont, activity.getString(R.string.quit_game), activity.getVertexBufferObjectManager());
		quitButton.setPosition(mCamera.getWidth() / 2 - startButton.getWidth() / 2, (mCamera.getHeight() / 2 - startButton.getHeight() / 2) + 50);
		addMenuItem(quitButton);
	}
	
	public void onKeyUp(int keyCode){
		if(keyCode == OuyaController.BUTTON_Y){
			this.activity.setCurrentScene(SloppyNinjaActivity.SceneIndex.GAME);
		}
		
		if(keyCode == OuyaController.BUTTON_O){
			this.activity.setCurrentScene(SloppyNinjaActivity.SceneIndex.SPLASH);
		}		
	}
	
	public void onKeyDown(int keyCode){
		
	}	
}
