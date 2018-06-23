package de.paluno.game;

import com.badlogic.gdx.InputAdapter;
import de.paluno.game.gameobjects.World;

public class UserWorldController extends WorldController {

    private WorldHandler worldHandler;

    private int wormMovement;
    private int angleRotationMovement;

    private InputAdapter inputAdapter = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Constants.KEY_MOVE_LEFT:
                    //worldHandler.applyWormMovement();
                    // move current worm left
                    break;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyUp(int keycode) {
            return super.keyUp(keycode);
        }
    };

    public void initialize(WorldHandler handler) {
        this.worldHandler = handler;
    }


}
