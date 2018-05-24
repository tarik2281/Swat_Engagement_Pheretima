package de.paluno.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import de.paluno.game.gameobjects.ShotDirectionIndicator;
import de.paluno.game.gameobjects.Worm;

public class InputHandler extends InputAdapter {

	private World world;
	
	private boolean shiftL = false;
	private boolean shiftR = false;
	private boolean ctrlL = false;
	private boolean ctrlR = false;
	
	/**
	 * Constructor
	 * @param world - Reference to the world we are sending our key orders to
	 */
	public InputHandler(World world) {
		this.world = world;
	}
	
	/**
	 * Super placeholder for parent's "Mouseclick" function
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return super.touchDown(screenX, screenY, pointer, button);
	}
	/**
	 * Super placeholder for parent's "Mouseup" function
	 */
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }
	/**
	 * Super placeholder for parent's "Mouse moved" function
	 */
	public boolean mouseMoved(int screenX, int screenY) {
        return super.mouseMoved(screenX, screenY);
    }
	
	/**
	 * Handler function for keyboard keys being pressed
	 * @param keycode - the Keys.keycode of the key that is pressed
	 * @return Was this change processed?
	 */
	public boolean keyDown(int keycode) {

        switch (keycode) {
        	// Common key codes
        	case Keys.SHIFT_LEFT:
        		this.shiftL = true;
        		return true;
        		break;
        	case Keys.SHIFT_RIGHT:
        		this.shiftR = true;
        		return true;
        		break;
        	case Keys.CONTROL_LEFT:
        		this.ctrlL = true;
        		return true;
        		break;
        	case Keys.CONTROL_RIGHT:
        		this.ctrlR = true;
        		return true;
        		break;
        	
            // Gameplay key codes
            case Constants.KEY_MOVE_LEFT:
                world.getCurrentPlayer().setMovement(Constants.MOVEMENT_LEFT, true);
                return true;
            case Constants.KEY_MOVE_RIGHT:
            	world.getCurrentPlayer().setMovement(Constants.MOVEMENT_RIGHT, true);
                return true;
            case Constants.KEY_JUMP:
                world.getCurrentPlayer().jump();
                return true;
            case Constants.KEY_DO_ACTION:
                world.action();
                return true;
            case Constants.KEY_ROTATE_INDICATOR_UP:
                if(world.getShotDirectionIndicator() != null) world.getShotDirectionIndicator().rotate(Constants.MOVEMENT_UP);
            	return true;
            case Constants.KEY_ROTATE_INDICATOR_DOWN:
            	if(world.getShotDirectionIndicator() != null) world.getShotDirectionIndicator().rotate(Constants.MOVEMENT_DOWN);
            	return true;

            // debugging key events
            case Constants.KEY_MOVE_CAMERA_LEFT:
                
                return true;
            case Constants.KEY_MOVE_CAMERA_RIGHT:
                
                return true;
            case Constants.KEY_MOVE_CAMERA_UP:
                
                return true;
            case Constants.KEY_MOVE_CAMERA_DOWN:
                
                return true;
            case Constants.KEY_TOGGLE_DEBUG_RENDER:
                
                return true;
            case Constants.KEY_TOGGLE_CAMERA_FOCUS:
                
                break;
        }

        return super.keyDown(keycode);
    }
	/**
	 * Handler function for keyboard keys being released
	 * @param keycode - The Keys.keycode of the key being released
	 * @return Was this change processed?
	 */
	public boolean keyUp(int keycode) {
        switch (keycode) {
            // Common key codes
	        case Keys.SHIFT_LEFT:
	    		this.shiftL = false;
	    		return true;
	    		break;
	    	case Keys.SHIFT_RIGHT:
	    		this.shiftR = false;
	    		return true;
	    		break;
	    	case Keys.CONTROL_LEFT:
	    		this.ctrlL = false;
	    		return true;
	    		break;
	    	case Keys.CONTROL_RIGHT:
	    		this.ctrlR = false;
	    		return true;
	    		break;
        	
        	// gameplay key events
            case Constants.KEY_MOVE_LEFT:
            	world.getCurrentPlayer().setMovement(Constants.MOVEMENT_LEFT, false);
                return true;
                break;
            case Constants.KEY_MOVE_RIGHT:
                world.getCurrentPlayer().setMovement(Constants.MOVEMENT_RIGHT, false);
                return true;
                break;
            case Constants.KEY_ROTATE_INDICATOR_UP:
            case Constants.KEY_ROTATE_INDICATOR_DOWN:
            	if(world.getShotDirectionIndicator() != null) world.getShotDirectionIndicator().rotate(Constants.MOVEMENT_NO_MOVEMENT);
            	return true;
            	break;

            // debugging key events
            case Constants.KEY_MOVE_CAMERA_LEFT:
                
                return true;
                break;
            case Constants.KEY_MOVE_CAMERA_RIGHT:
                
                return true;
                break;
            case Constants.KEY_MOVE_CAMERA_UP:
                
                return true;
                break;
            case Constants.KEY_MOVE_CAMERA_DOWN:
                
                return true;
                break;
            default: return super.keyUp(keycode); break;
        }
    }
	
	/**
	 * Getter method to get the state of the shift key being pressed
	 * @param optional LoR - Only return "Left or Right" state
	 * @return Shift key(s) pressed?
	 */
	public boolean isShift() {return (shiftL || shiftR);}
	public boolean isShift(char LoR) {
		if(LoR == 'l' || LoR == 'L') return shiftL;
		else if(LoR == 'r' || LoR == 'R') return shiftR;
		else return false;
	}
	/**
	 * Getter method to get the state of the ctrl key being pressed
	 * @param optional LoR - Only return "Left or Right" state
	 * @return Ctrl key(s) pressed?
	 */
	public boolean isCtrl() {return (ctrlL || ctrlR);}
	public boolean isCtrl(char LoR) {
		if(LoR == 'l' || LoR == 'L') return ctrlL;
		else if(LoR == 'r' || LoR == 'R') return ctrlR;
		else return false;
	}
	
	//DONUT REMOVE DIS CLASS AGAIN

}
