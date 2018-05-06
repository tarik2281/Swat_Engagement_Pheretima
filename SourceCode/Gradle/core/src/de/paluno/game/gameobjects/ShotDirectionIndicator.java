package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import de.paluno.game.GameState;
import de.paluno.game.screens.PlayScreen;

public class ShotDirectionIndicator extends java.lang.Object implements Renderable, Updatable{
	
	private PlayScreen playScreen;
	private Worm worm;
	private int playerNumber;
	private float positionX;
	private float positionY;
	private float mousePositionX;
	private float mousePositionY;
	private Sprite sprite;
	private Sprite sprite2;
	private SpriteBatch batch;
	private GameState gamestate;
	private Texture texture;
	private Texture texture2;
	
	
	public ShotDirectionIndicator(int playerNumber, Worm worm, PlayScreen playScreen) {
		this.playerNumber = playerNumber;
		this.worm = worm;
		this.playScreen = playScreen;
		
		texture = new Texture(Gdx.files.internal("Projectile.png"));
		texture2 = new Texture(Gdx.files.internal("Pfeil.png"));
		sprite = new Sprite(texture);
		sprite2 = new Sprite(texture2);
				
	}

	public void update(float delta, GameState gamestate) {
		this.gamestate = gamestate;
	}
	
	public void render(SpriteBatch batch, float delta){
		positionX = worm.getBody().getPosition().x;
	    positionY = worm.getBody().getPosition().y;
	    
	    mousePositionX = Gdx.input.getX();
	    mousePositionY = Gdx.input.getY();
		
		batch.begin();
		batch.draw(texture, positionX + 3, positionY + 3);
		batch.end();
		}
		
	}

