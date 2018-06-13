package de.paluno.game;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class InputHandler extends InputAdapter {
	
	/**
	 * Blueprint on how a KeyListener registered here should look like
	 */
	public interface KeyListener {
		boolean onKeyEvent(int keyCode, boolean keyDown);
	}
	
	private boolean shiftL = false;
	private boolean shiftR = false;
	private boolean ctrlL = false;
	private boolean ctrlR = false;
	
	private int mouseX = -1;
	private int mouseY = -1;

	private HashMap<Integer, ArrayList<KeyListener>> keyListenerMap = new HashMap<>();
	
	private static InputHandler instance = null;
	
	/**
	 * Empty constructor for use with new register handling mechanic
	 */
	public InputHandler() {}
	
	/**
	 * Method to register a new KeyListener for a given key
	 * @param keyCode - The Input.Keys code to listen to
	 * @param l - The KeyListener Object to execute when key is pressed/released
	 */
	public void registerKeyListener(int keyCode, KeyListener l) {
		keyListenerMap.computeIfAbsent(keyCode, k -> new ArrayList<>(1)).add(l);
		// Take map, create new empty ArrayList<KeyListener> when empty, add KeyListener
	}
	/**
	 * Method to unregister a given KeyListener for a given key
	 * @param keyCode - The Input.Keys code we are listening to
	 * @param l - The KeyListener to remove
	 */
	public void unregisterKeyListener(int keyCode, KeyListener l) {
		ArrayList<KeyListener> listeners = keyListenerMap.get(keyCode);

		if (listeners != null) {
			listeners.remove(l);
		}
	}
	
	/**
	 * Super placeholder for parent's "Mouseclick" function
	 */
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouseX = screenX;
		mouseY = screenY;
		
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
		mouseX = screenX;
		mouseY = screenY;
		
		return super.mouseMoved(screenX, screenY);
    }
	
	/**
	 * Handler function for keyboard keys being pressed
	 * @param keyCode - the Keys.keycode of the key that is pressed
	 * @return Was this change processed?
	 */
	public boolean keyDown(int keyCode) {
		
		boolean handled = false;
        switch (keyCode) {
        	// Common key codes
        	case Keys.SHIFT_LEFT:
        		this.shiftL = true;
        		handled = true;
        		break;
        	case Keys.SHIFT_RIGHT:
        		this.shiftR = true;
        		handled = true;
        		break;
        	case Keys.CONTROL_LEFT:
        		this.ctrlL = true;
        		handled = true;
        		break;
        	case Keys.CONTROL_RIGHT:
        		this.ctrlR = true;
        		handled = true;
        		break;
        }

        ArrayList<KeyListener> listeners = keyListenerMap.get(keyCode);
        if (listeners != null) {
			for (KeyListener l : listeners) {
				if (handled = l.onKeyEvent(keyCode, true))
					break;
			}
		}

        return handled;
    }
	/**
	 * Handler function for keyboard keys being released
	 * @param keyCode - The Keys.keycode of the key being released
	 * @return Was this change processed?
	 */
	public boolean keyUp(int keyCode) {
        boolean handled = false;
		switch (keyCode) {
            // Common key codes
	        case Keys.SHIFT_LEFT:
	    		this.shiftL = false;
	    		handled = true;
	    		break;
	    	case Keys.SHIFT_RIGHT:
	    		this.shiftR = false;
	    		handled = true;
	    		break;
	    	case Keys.CONTROL_LEFT:
	    		this.ctrlL = false;
	    		handled = true;
	    		break;
	    	case Keys.CONTROL_RIGHT:
	    		this.ctrlR = false;
	    		handled = true;
	    		break;
		}

		ArrayList<KeyListener> listeners = keyListenerMap.get(keyCode);
		if (listeners != null) {
			for (KeyListener l : listeners) {
				if (handled = l.onKeyEvent(keyCode, false))
					break;
			}
		}

		return handled;
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
	/**
	 * Getter method for the cursors current (or last known) position
	 * @return Vector mouseX, mouseY
	 */
	public Vector2 getMousePosition() {return new Vector2(mouseX, mouseY);}
	
	/**
	 * Static reference to the InputHandler, to make it globally available
	 * @return this
	 */
	public static InputHandler getInstance() {
		if(instance == null) {
			instance = new InputHandler();
			Gdx.input.setInputProcessor(instance);
		}
		return instance;
	}
}