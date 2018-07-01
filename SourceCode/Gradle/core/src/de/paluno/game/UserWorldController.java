package de.paluno.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import de.paluno.game.gameobjects.WeaponType;

public class UserWorldController {

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
                case Constants.KEY_ROTATE_INDICATOR_UP:
                    angleRotationMovement = Constants.MOVEMENT_UP;
                    worldHandler.applyShotDirectionMovement(angleRotationMovement);
                    break;
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    angleRotationMovement = Constants.MOVEMENT_DOWN;
                    worldHandler.applyShotDirectionMovement(angleRotationMovement);
                    break;
                case Constants.KEY_DO_ACTION:
                    worldHandler.shoot();
                    break;
                case Constants.KEY_SELECT_WEAPON_1:
                    worldHandler.applyEquipWeapon(WeaponType.WEAPON_GUN);
                    break;
                case Constants.KEY_SELECT_WEAPON_2:
                    worldHandler.applyEquipWeapon(WeaponType.WEAPON_GRENADE);
                    break;
                case Constants.KEY_SELECT_WEAPON_3:
                    worldHandler.applyEquipWeapon(WeaponType.WEAPON_BAZOOKA);
                    break;
                case Constants.KEY_SELECT_WEAPON_4:
                    worldHandler.applyEquipWeapon(WeaponType.WEAPON_SPECIAL);
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
                case Constants.KEY_ROTATE_INDICATOR_UP:
                    if (angleRotationMovement == Constants.MOVEMENT_UP) {
                        angleRotationMovement = Constants.MOVEMENT_NO_MOVEMENT;
                        worldHandler.applyShotDirectionMovement(angleRotationMovement);
                    }
                    break;
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    if (angleRotationMovement == Constants.MOVEMENT_DOWN) {
                        angleRotationMovement = Constants.MOVEMENT_NO_MOVEMENT;
                        worldHandler.applyShotDirectionMovement(angleRotationMovement);
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
