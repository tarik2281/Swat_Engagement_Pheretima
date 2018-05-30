package de.paluno.game.gameobjects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;

import de.paluno.game.Constants;
import de.paluno.game.GameState;

public class Player implements Updatable {
	private int playerNum;
	
	private int characterNum;
	private Worm[] characters;
	private int turn = 1;
	
	private World world;
	private AssetManager assets;
	
	private GameState gameState;
	
	/**
	 * Empty constructor for cloning purposes
	 */
	public Player() {}
	/**
	 * Constructor
	 * @param charNum - Number of characters this player begins with
	 * @param playerNum - Player Number of this player
	 * @param world - Reference to the world we are playing in
	 * @param assets - Reference to the global Asset Manager
	 */
	public Player(int playerNum, World world, AssetManager assets) {
		
		this.characterNum = Constants.MAX_CHAR_NUM;
		this.playerNum = playerNum;
		this.world = world;
		
		this.characters = new Worm[characterNum];
		
		for(int i = 0; i < characterNum; i++) {
			characters[i] = new Worm(this, i+1);
		}
		
		this.assets = assets;
	}
	/**
	 * Handler for GameLoop's update cycle - needed from Interface updatable
	 * This dependency is just to update the GameState for the players directly, instead of having 5 cross-communications with it's Worms
	 * @param delta - Time since last update in seconds
	 * @param state - Current GameState
	 */
	public void update(float delta, GameState state) {this.gameState = state;}
	
	/**
	 * Getter method for this player's player number
	 * @return - player number
	 */
	public int getPlayerNumber() {return this.playerNum;}
	/**
	 * Getter method for the character's turn
	 * @return turn
	 */
	public int getTurn() {return this.turn;}
	/**
	 * Hard setter method for the character's turn 
	 * @param turn - int 1 - MAX_CHAR_NUM: Character number whose turn it is now
	 */
	protected void setTurn(int turn) {
		if(turn < 1) this.turn = 1;
		else if(this.turn > Constants.MAX_CHAR_NUM) this.turn = Constants.MAX_CHAR_NUM;
		else this.turn = turn;
	}
	/**
	 * Soft setter method for the character's turn
	 * Shift through all still available characters to find the next one whose turn it is
	 */
	protected void shiftTurn() {
		if(characterNum == 0) return;
		this.turn++;
		if(turn > characterNum) turn = 1;
		if(characters[turn-1] == null) shiftTurn();
	}
	/**
	 * Getter Method for the reference to the Asset Manager
	 * @return AssetManager
	 */
	public AssetManager getAssets() {return this.assets;}
	/**
	 * Getter method for the world we are playing in
	 * @return world
	 */
	public World getWorld() {return this.world;}
	
	/**
	 * Soft setter method for characterNumber - Set Character as KIA and remove it
	 * @param charNum - The number of the character that died
	 */
	protected void characterDied(int charNum) {
		// No characters anymore or this one allready dead? Nothing to do here.
		if(this.characterNum <= 0 || this.characters[charNum-1] == null) return;
		this.characters[charNum-1] = null;
		this.characterNum--;
	}
	
	//TODO: Make deprecated by managing gamestate globally, e.g. in world.
	/**
	 * Setter method for current Game State
	 * @param newState - New game state
	 */
	protected void setGameState(GameState newState) {this.gameState = newState;}
	/**
	 * Getter method for current Game State
	 * @return gameState
	 */
	public GameState getGameState() {return this.gameState;}
	
	/**
	 * Getter method for player's turn status
	 * @return Is it this player's turn?
	 */
	public boolean isPlayerTurn() {
		return (this.playerNum == 1 && this.gameState == GameState.PLAYERONETURN)
				||
				(this.playerNum == 2 && this.gameState == GameState.PLAYERTWOTURN);
	}
	
	/**
	 * Getter method for the character whose turn it currently is
	 * @return Worm
	 */
	public Worm getCurrentWorm() {return this.characters[this.turn-1];}
	
	/**
	 * Passthrough method to give move order to the currently movable worm
	 * @param code - Constants.MOVEMENT_... integer for the movement code
	 */
	public void setMovement(int code) {if(getCurrentWorm() != null) getCurrentWorm().setMovement(code);}
	/**
	 * Passthrough method to give a jump order to the currently movable worm
	 */
	public void jump() {if(getCurrentWorm() != null) getCurrentWorm().setJump(true);}
	/**
	 * Passthrough method to give a shoot order to the currently movable worm
	 */
	public void shoot() {if(getCurrentWorm() != null) getCurrentWorm().shoot();}
	
	public Player clone() {
		Player clone = new Player();
		clone.setCloningParameters(this);
		return clone;
	}
	
	public void setCloningParameters(Player copy) {
		this.playerNum = copy.playerNum;
		
		this.characterNum = copy.characterNum;
		this.characters = copy.characters;
		this.turn = copy.turn;
		
		this.world = copy.world;
		this.assets = copy.assets;
		
		this.gameState = this.gameState;
	}
}
