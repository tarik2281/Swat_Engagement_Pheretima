package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.paluno.game.Assets;
import de.paluno.game.SEPGame;

public class GameOverScreen extends com.badlogic.gdx.ScreenAdapter implements Loadable {
	
	protected Sprite sprite;
	private SpriteBatch batch;
	private WinningPlayer winningPlayer;
	private SEPGame game;
	private Stage restartStage;
	private Table table;
	private ImageButton restartButton;
	private Texture restartButtonTexture;
	private TextureRegion restartButtonTextureRegion;
	private TextureRegionDrawable restartButtonTextureDrawable;
	private Music overSound;
	
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
		table = new Table();
		
		Texture texture = null;
		
		overSound = game.getAssetManager().get(Assets.gameOverSound);
		overSound.setLooping(false);
		overSound.setVolume(0.1f);switch (winningPlayer) {
		case PLAYERONE:
			overSound.play();texture = game.getAssetManager().get(Assets.gameOverScreen1);
			break;
		case PLAYERTWO:overSound.play();
			texture = game.getAssetManager().get(Assets.gameOverScreen2);
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
                game.setStartScreen();
            }
        });

        restartStage.addActor(table);
        table.add(restartButton);
        table.setFillParent(true);
        table.bottom();
        table.padBottom(100);
        table.padRight(30);
	}
	
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		sprite.draw(batch);
		batch.end();
		
		restartStage.act(delta);
		restartStage.draw();
	}
	
	public void hide(){
		batch.dispose();
		restartStage.dispose();
	overSound.dispose();}
}