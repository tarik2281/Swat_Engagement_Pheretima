package de.paluno.game;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import de.paluno.game.gameobjects.ShotDirectionIndicator;
import de.paluno.game.gameobjects.WeaponIndicator;
import de.paluno.game.gameobjects.WeaponType;
import de.paluno.game.worldhandlers.WorldHandler;

public class UserWorldController {

    private WorldHandler worldHandler;

    private int cameraVerticalMovement;
    private int cameraHorizontalMovement;
    private int wormMovement;
    private int angleRotationMovement;

    private InputAdapter inputAdapter = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Constants.KEY_MOVE_CAMERA_LEFT:
                    startCamHorizMovement(Constants.MOVEMENT_LEFT);
                    break;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    startCamHorizMovement(Constants.MOVEMENT_RIGHT);
                    break;
                case Constants.KEY_MOVE_CAMERA_UP:
                    startCamVertMovement(Constants.MOVEMENT_UP);
                    break;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    startCamVertMovement(Constants.MOVEMENT_DOWN);
                    break;
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
                {
                    WeaponIndicator indicator = worldHandler.getCurrentWeaponIndicator();
                    if (indicator instanceof ShotDirectionIndicator) {
                        ((ShotDirectionIndicator) indicator).setRotationMovement(angleRotationMovement);
                    }
                }
                    break;
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    angleRotationMovement = Constants.MOVEMENT_DOWN;
                {
                    WeaponIndicator indicator = worldHandler.getCurrentWeaponIndicator();
                    if (indicator instanceof ShotDirectionIndicator) {
                        ((ShotDirectionIndicator) indicator).setRotationMovement(angleRotationMovement);
                    }
                }
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
                case Constants.KEY_SELECT_WEAPON_5:
                	worldHandler.applyEquipWeapon(WeaponType.WEAPON_AIRSTRIKE);
                	break;
                case Constants.KEY_SELECT_WEAPON_6:
                	worldHandler.applyEquipWeapon(WeaponType.WEAPON_MINE);
                	break;
                case Constants.KEY_SELECT_WEAPON_7:
                	worldHandler.applyEquipWeapon(WeaponType.WEAPON_TURRET);
                	break;
                case Constants.KEY_TOGGLE_DEBUG_RENDER:
                    worldHandler.toggleDebugRender();
                    break;
                case Constants.KEY_DEBUG_DROP_TURRET:
                    worldHandler.randomAirdrop();
                    break;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Constants.KEY_MOVE_CAMERA_LEFT:
                    stopCamHorizMovement(Constants.MOVEMENT_LEFT);
                    break;
                case Constants.KEY_MOVE_CAMERA_RIGHT:
                    stopCamHorizMovement(Constants.MOVEMENT_RIGHT);
                    break;
                case Constants.KEY_MOVE_CAMERA_UP:
                    stopCamVertMovement(Constants.MOVEMENT_UP);
                    break;
                case Constants.KEY_MOVE_CAMERA_DOWN:
                    stopCamVertMovement(Constants.MOVEMENT_DOWN);
                    break;
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
                        {
                            WeaponIndicator indicator = worldHandler.getCurrentWeaponIndicator();
                            if (indicator instanceof ShotDirectionIndicator) {
                                ((ShotDirectionIndicator) indicator).setRotationMovement(angleRotationMovement);
                            }
                        }
                    }
                    break;
                case Constants.KEY_ROTATE_INDICATOR_DOWN:
                    if (angleRotationMovement == Constants.MOVEMENT_DOWN) {
                        angleRotationMovement = Constants.MOVEMENT_NO_MOVEMENT;
                        {
                            WeaponIndicator indicator = worldHandler.getCurrentWeaponIndicator();
                            if (indicator instanceof ShotDirectionIndicator) {
                                ((ShotDirectionIndicator) indicator).setRotationMovement(angleRotationMovement);
                            }
                        }
                    }
                    break;
            }
            return super.keyUp(keycode);
        }
    };

    private void startCamVertMovement(int movement) {
        cameraVerticalMovement = movement;
        worldHandler.applyCameraMovement(cameraVerticalMovement, cameraHorizontalMovement);
    }

    private void startCamHorizMovement(int movement) {
        cameraHorizontalMovement = movement;
        worldHandler.applyCameraMovement(cameraVerticalMovement, cameraHorizontalMovement);
    }

    private void stopCamHorizMovement(int movement) {
        if (cameraHorizontalMovement == movement)
            cameraHorizontalMovement = Constants.MOVEMENT_NO_MOVEMENT;

        worldHandler.applyCameraMovement(cameraVerticalMovement, cameraHorizontalMovement);
    }

    private void stopCamVertMovement(int movement) {
        if (cameraVerticalMovement == movement)
            cameraVerticalMovement = Constants.MOVEMENT_NO_MOVEMENT;

        worldHandler.applyCameraMovement(cameraVerticalMovement, cameraHorizontalMovement);
    }

    public void initialize(WorldHandler handler) {
        this.worldHandler = handler;
    }

    public InputProcessor getInputProcessor() {
        return inputAdapter;
    }

}
