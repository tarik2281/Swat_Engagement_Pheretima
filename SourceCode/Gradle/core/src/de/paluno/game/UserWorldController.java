package de.paluno.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

public class UserWorldController extends WorldController {

    private WorldHandler worldHandler;

    private int wormMovement;
    private int angleRotationMovement;

    private InputAdapter inputAdapter = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Constants.KEY_MOVE_LEFT:
                    wormMovement = Constants.MOVEMENT_LEFT;
                    worldHandler.applyWormMovement(wormMovement);
                    // move current worm left
                    break;
                case Constants.KEY_MOVE_RIGHT:
                    wormMovement = Constants.MOVEMENT_RIGHT;
                    worldHandler.applyWormMovement(wormMovement);
                    break;
                case Constants.KEY_JUMP:
                    worldHandler.applyWormJump();
                    break;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Constants.KEY_MOVE_LEFT:
                    if (wormMovement == Constants.MOVEMENT_LEFT) {
                        wormMovement = Constants.MOVEMENT_NO_MOVEMENT;
                        worldHandler.applyWormMovement(wormMovement);
                    }
                    break;
                case Constants.KEY_MOVE_RIGHT:
                    if (wormMovement == Constants.MOVEMENT_RIGHT) {
                        wormMovement = Constants.MOVEMENT_NO_MOVEMENT;
                        worldHandler.applyWormMovement(wormMovement);
                    }
                    break;
            }
            return super.keyUp(keycode);
        }
    };

    public void initialize(WorldHandler handler) {
        this.worldHandler = handler;
    }

    public InputProcessor getInputProcessor() {
        return inputAdapter;
    }

}
