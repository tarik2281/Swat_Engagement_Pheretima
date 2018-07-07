package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.paluno.game.Assets;
import de.paluno.game.SEPGame;
import de.paluno.game.gameobjects.Worm;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter implements Loadable {
	
	protected Sprite sprite;
	private SpriteBatch batch;
	private WinningPlayer winningPlayer;
	private SEPGame game;
	private Stage restartStage;
	private Table table;
	private Label label;
	private ImageButton restartButton;
	private Texture restartButtonTexture;
	private TextureRegion restartButtonTextureRegion;
	private TextureRegionDrawable restartButtonTextureDrawable;
	private Music overSound;
	private BitmapFont font;
	private CharSequence str;
	private Skin skin;
	private boolean first;
	
	
	public GameOverScreen(SEPGame game, WinningPlayer winningPlayer) {
		this.winningPlayer = winningPlayer;
		this.game = game;
	}

	@Override
	public boolean load(AssetManager manager) {
		Assets.loadAssets(manager, Assets.GameOverScreenAssets);
		Assets.loadAssets(manager, Assets.Music);
		return false;
	}

	public void show() {
		//GameOverScreen
		batch = new SpriteBatch();
		table = new Table(skin);
		label = new Label(str, skin);
		font = new BitmapFont();
		skin = new Skin(Gdx.files.internal("sgx-ui/sgx-ui.json"));
		
		Texture texture = null;
		
		overSound = game.getAssetManager().get(Assets.gameOverSound);
		overSound.setLooping(false);
		overSound.setVolume(1f);
		
		switch (winningPlayer) {
			case PLAYERONE:
				overSound.play();
				first = true;
				texture = game.getAssetManager().get(Assets.gameOverScreen);
				break;
			case PLAYERTWO:
				overSound.play();
				first = false;
				texture = game.getAssetManager().get(Assets.gameOverScreen);
				break;
		}
		sprite = new Sprite(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());	
		
		//Restart button
		restartStage = new Stage();
		Gdx.input.setInputProcessor(restartStage);
		
		restartButtonTexture = game.getAssetManager().get(Assets.menuButton);
        restartButtonTextureRegion = new TextureRegion(restartButtonTexture);
        restartButtonTextureDrawable = new TextureRegionDrawable(restartButtonTextureRegion);
        restartButton = new ImageButton(restartButtonTextureDrawable);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMenuScreen();
            }
        });
        
        restartStage.addActor(table);
        table.add(label);
        table.add(restartButton);
        table.setFillParent(true);
        table.bottom();
        table.padBottom(100);
        table.padRight(30);
	}
	
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		if(first) {
			font.draw(batch, "hat gewonnen.", 325, 425);
		}else {
			font.draw(batch, "hat gewonnen.", 325, 425);
		}		
		sprite.draw(batch);
		batch.end();
		
		restartStage.act(delta);
		restartStage.draw();
	}
	
	public void hide(){
		batch.dispose();
		restartStage.dispose();
		overSound.dispose();
		font.dispose();
	}
}