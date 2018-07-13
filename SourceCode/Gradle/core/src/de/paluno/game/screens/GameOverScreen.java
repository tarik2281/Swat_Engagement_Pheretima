package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.EventManager;
import de.paluno.game.SEPGame;
import de.paluno.game.gameobjects.Worm;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter implements Loadable {
	
	protected Sprite sprite;
	private SpriteBatch batch;
	private WinningPlayer winningPlayer;
	private SEPGame game;
	private Stage stage;
	private Table table;
	private Table myFontTable;
	private TextButton restartButton;
	private Texture texture;
	private Texture restartButtonTexture;
	private TextureRegion restartButtonTextureRegion;
	private TextureRegionDrawable restartButtonTextureDrawable;
	private Label.LabelStyle labelStyle;
	private BitmapFont myFont;
	private Label label;
	private CharSequence text;
	private String playerName;
	private Sound gameOver;
	private Skin skin;
	
	
	public GameOverScreen(SEPGame game, String playerName) {
		this.game = game;
		this.playerName = playerName;
	}

	@Override
	public boolean load(AssetManager manager) {
		Assets.loadAssets(manager, Assets.GameOverScreenAssets);
		Assets.loadAssets(manager, Assets.Music);
		return false;
	}

	public void show() {
		//GameOverScreen
		//batch = new SpriteBatch();
		table = new Table();
		myFontTable = new Table();
		labelStyle = new Label.LabelStyle();

		ElementGUI elementGUI = new ElementGUI();
		skin = elementGUI.getSkin();
		
		//Sound
		gameOver = game.getAssetManager().get(Assets.gameOverSound);
		gameOver.play();

		Image gameOverImage = elementGUI.createBackground(game.getAssetManager().get(Assets.gameOverScreen));
		//texture = game.getAssetManager().get(Assets.gameOverScreen);
		//sprite = new Sprite(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Restart button
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		restartButton = new TextButton("Menu", skin);

		//restartButtonTexture = game.getAssetManager().get(Assets.menuButton);
        //restartButtonTextureRegion = new TextureRegion(restartButtonTexture);
        //restartButtonTextureDrawable = new TextureRegionDrawable(restartButtonTextureRegion);
        //restartButton = new ImageButton(restartButtonTextureDrawable);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EventManager.getInstance().queueEvent(EventManager.Type.ClickSound, null);
                game.setModiScreen();
            }
        });
        
        //BitmapFont
        myFont = new BitmapFont(Gdx.files.internal("GameOverScreenFonts.fnt"));
        labelStyle.font = myFont;
        
        String labelText = null;
        
        if (playerName != null)
        	labelText = playerName + " hat gewonnen.";
        else
        	labelText = "Das Spiel ist unentschieden.";
        
		label = new Label(labelText, labelStyle);
        
        stage.addActor(table);
        //stage.addActor(myFontTable);

        table.setFillParent(true);
        table.center();
        table.add(gameOverImage);
        table.row();
        table.add(label).pad(50);
        table.row();
        table.add(restartButton).size(200, 60).pad(50);

        //myFontTable.add(label);
        //myFontTable.setFillParent(true);
        //myFontTable.center();
        //myFontTable.padBottom(100);
        
        //table.add(restartButton);
        //table.setFillParent(true);
        //table.bottom();
        //table.padBottom(200);
        //table.padRight(30);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//batch.begin();
		//sprite.draw(batch);
		//batch.end();
		
		stage.act(delta);
		stage.draw();
	}
	
	public void hide(){
		myFont.dispose();
		skin.dispose();
		//batch.dispose();
		stage.dispose();
	}
}