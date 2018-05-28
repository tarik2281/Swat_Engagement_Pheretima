package de.paluno.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import de.paluno.game.gameobjects.ShotDirectionIndicator;
import de.paluno.game.gameobjects.Worm;

public class InputHandler extends InputAdapter {

	//private World world;
	//TODO: Cleanup
	
	private boolean shiftL = false;
	private boolean shiftR = false;
	private boolean ctrlL = false;
	private boolean ctrlR = false;
	
	private ArrayList<Integer> registeredKeyCodesDown = new ArrayList();
	private HashMap<Integer, ArrayList<Method>> registeredHandlersDown = new HashMap();
	private ArrayList<Integer> registeredKeyCodesUp = new ArrayList();
	private HashMap<Integer, ArrayList<Method>> registeredHandlersUp = new HashMap();
	private ArrayList<Method> registeredMouseListeners = new ArrayList();
	
	private static InputHandler instance = null;
	
	/**
	 * Empty constructor for use with new register handling mechanic
	 */
	public InputHandler() {
		//instance = this;
		//Gdx.input.setInputProcessor(instance);
	}
	/**
	 * Constructor
	 * @param world - Reference to the world we are sending our key orders to
	 */
	/*public InputHandler(World world) {
		this.world = world;
	}*/
	
	/**
	 * Function to register the @handler function of an @target object to the @keycode given
	 * @param keycode - Keys.keycode to listen to
	 * @param target - Target object to send order to
	 * @param handler - Name of the function to call within the target object
	 * @param up - Should this handler be for the keyUp-Event?
	 * @return Successfully registered?
	 */
	public boolean registerHandler(int keycode, Object target, String handler, boolean up) {
		// Try to get the target's method, to see if it actually exists.
		// If not, we don't need to bother with it anymore, just pop an error message to inform the caller.
		Method method;
		try {
			method = target.getClass().getMethod(handler, Integer.class);
		} catch(SecurityException e) {System.err.println("Security violation on input registration - target method not public!"); return false;}
		catch(NoSuchMethodException e) {System.err.println("Method to be registered not found or with invalid parameters!"); return false;}
		
		// Reference our lists/maps according to event to listen to
		HashMap<Integer, ArrayList<Method>> registeredHandlers;
		ArrayList<Integer> registeredKeyCodes;
		if(up) {registeredHandlers = registeredHandlersUp; registeredKeyCodes = registeredKeyCodesUp;}
		else {registeredHandlers = registeredHandlersDown; registeredKeyCodes = registeredKeyCodesUp;}
		
		// Now see if this method is allready registered to a key - if not, do it now!
		ArrayList<Method> i;
		if(!registeredHandlers.containsKey(keycode)) {
			// No previous mapping for this keycode
			i = new ArrayList();
			i.add(method);
			registeredHandlers.put(keycode, i);
		} else {
			// Previous mapping - fetch, add, re-put
			i = registeredHandlers.get(keycode);
			if(i.contains(method)) return false;
			else {
				i.add(method);
				registeredHandlers.put(keycode, i);
			}
		}
		
		// Finally, register the key to listen to, if not allready happened
		if(!registeredKeyCodes.contains(keycode)) registeredKeyCodes.add(keycode);
		
		return true;
	}
	/**
	 * Method to remove a handler for a certain keycode
	 * @param keycode - Keys.keycode of the key this method is registered to
	 * @param target - Object to look in
	 * @param handler - Name of the function to look for
	 * @param up - Is this handler for the keyUp-Event registered?
	 * @return Successfully removed something?
	 */
	public boolean deregisterHandler(Integer keycode, Object target, String handler, boolean up) {
		// Try to get the target's method, to see if it actually exists.
		// If not, we don't need to bother with it anymore, just pop an error message to inform the caller.
		Method method;
		try {
			method = target.getClass().getMethod(handler, Integer.class);
		} catch(SecurityException e) {System.err.println("Security violation on input deregistration - target method not public!"); return false;}
		catch(NoSuchMethodException e) {System.err.println("Method to be deregistered not found or with invalid parameters!"); return false;}
		
		// Reference our lists/maps according to event to listen to
		HashMap<Integer, ArrayList<Method>> registeredHandlers;
		ArrayList<Integer> registeredKeyCodes;
		if(up) {registeredHandlers = registeredHandlersUp; registeredKeyCodes = registeredKeyCodesUp;}
		else {registeredHandlers = registeredHandlersDown; registeredKeyCodes = registeredKeyCodesUp;}
		
		// Now see if there actually is that method registered!
		ArrayList<Method> i;
		if(!registeredHandlers.containsKey(keycode)) {
			// No mapping for this keycode at all - nothing to do.
			return false;
		} else {
			// There is mapping for this keycode, let's look at it
			i = registeredHandlers.get(keycode);
			// Nope, this method isn't registered. Nothing to do.
			if(!i.contains(method)) return false;
			else {
				// So we have this method registered? Not anymore!
				i.remove(method);
				if(i.size() == 0) {
					// Oh, this list is now empty - therefor we don't even need to listen to this key anymore!
					registeredHandlers.remove(keycode);
					registeredKeyCodes.remove(keycode);
				}
			}
		}
		return true;
	}
	/**
	 * Method to add a mouseListener, receiving all updates of the cursor's position
	 * @param target - Object to send the data to
	 * @param handler - Name of the function that should receive it
	 * @return - Was this listener added successfully?
	 */
	public boolean registerMouseListener(Object target, String handler) {
		// Try to get the target's method, to see if it actually exists.
		// If not, we don't need to bother with it anymore, just pop an error message to inform the caller.
		Method method;
		try {
			method = target.getClass().getMethod(handler, Vector2.class);
		} catch(SecurityException e) {System.err.println("Security violation on mouse registration - target method not public!"); return false;}
		catch(NoSuchMethodException e) {System.err.println("Method to be registered not found or with invalid parameters!"); return false;}
		
		if(!registeredMouseListeners.contains(method)) registeredMouseListeners.add(method);
		else return false;
		
		return true;
	}
	/**
	 * Method to remove a mouseListener
	 * @param target - Object to lookup the method from
	 * @param handler - Name of the function that should be removed
	 * @return - Was this listener removed successfully?
	 */
	public boolean deregisterMouseListener(Object target, String handler) {
		// Try to get the target's method, to see if it actually exists.
		// If not, we don't need to bother with it anymore, just pop an error message to inform the caller.
		Method method;
		try {
			method = target.getClass().getMethod(handler, Vector2.class);
		} catch(SecurityException e) {System.err.println("Security violation on mouse deregistration - target method not public!"); return false;}
		catch(NoSuchMethodException e) {System.err.println("Method to be deregistered not found or with invalid parameters!"); return false;}
		
		if(registeredMouseListeners.contains(method)) registeredMouseListeners.remove(method);
		else return false;
		
		return true;
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
		boolean handled = false;
		for(Method m : registeredMouseListeners) {
    		try {
        		m.invoke(this, new Vector2(screenX, screenY));
        		handled = true;
        	} catch(InvocationTargetException e) {System.err.println("Invocation target invalid!");}
        	catch(IllegalArgumentException e) {System.err.println("Parameter on handler call not accepted!");}
        	catch(IllegalAccessException e) {System.err.println("Illegal access on handler call!");}
    	}
		
		if(handled) return true;
		else return super.mouseMoved(screenX, screenY);
    }
	
	/**
	 * Handler function for keyboard keys being pressed
	 * @param keycode - the Keys.keycode of the key that is pressed
	 * @return Was this change processed?
	 */
	public boolean keyDown(int keycode) {
		
		boolean handled = false;
        switch (keycode) {
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
            
        if(registeredKeyCodesDown.contains(keycode)) {
        	// We have (a) handler(s) registered for this key - iterate and execute!
        	ArrayList<Method> handlers = registeredHandlersDown.get(keycode);
        	for(Method m : handlers) {
        		try {
            		m.invoke(this, keycode);
            		handled = true;
            	} catch(InvocationTargetException e) {System.err.println("Invocation target invalid!");}
            	catch(IllegalArgumentException e) {System.err.println("Parameter on handler call not accepted!");}
            	catch(IllegalAccessException e) {System.err.println("Illegal access on handler call!");}
        	}
        }
        
        if(handled) return true;
        else return super.keyDown(keycode);
        
        	/*// Gameplay key codes
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
            default: return super.keyDown(keycode);
        }
        */
    }
	/**
	 * Handler function for keyboard keys being released
	 * @param keycode - The Keys.keycode of the key being released
	 * @return Was this change processed?
	 */
	public boolean keyUp(int keycode) {
        boolean handled = false;
		switch (keycode) {
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
		
		if(registeredKeyCodesUp.contains(keycode)) {
        	// We have (a) handler(s) registered for this keycode - iterate and execute!
        	ArrayList<Method> handlers = registeredHandlersUp.get(keycode);
        	for(Method m : handlers) {
        		try {
            		m.invoke(this, keycode);
            		handled = true;
            	} catch(InvocationTargetException e) {System.err.println("Invocation target invalid!");}
            	catch(IllegalArgumentException e) {System.err.println("Parameter on handler call not accepted!");}
            	catch(IllegalAccessException e) {System.err.println("Illegal access on handler call!");}
        	}
        }
        
        if(handled) return true;
        else return super.keyUp(keycode);
        	
        	/*// gameplay key events
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
        }*/
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