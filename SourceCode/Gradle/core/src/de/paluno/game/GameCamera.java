package de.paluno.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.PhysicsObject;

public class GameCamera {

    private OrthographicCamera camera;
    private OrthographicCamera debugCamera;
    private PhysicsObject cameraFocus;

    private float viewportWidth;
    private float viewportHeight;

    private Vector2 debugPosition;
    private Vector2 position;

    private int horizontalMovement;
    private int verticalMovement;

    // in world space
    private float bottomLimit;

    /**
     *
     * @param viewportWidth in pixels
     * @param viewportHeight in pixels
     */
    public GameCamera(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, viewportWidth * Constants.WORLD_SCALE,
                viewportHeight * Constants.WORLD_SCALE);

        debugPosition = new Vector2();
        position = new Vector2();
    }

    public void setCameraFocus(PhysicsObject object) {
        cameraFocus = object;
    }

    public PhysicsObject getCameraFocus() {
        return cameraFocus;
    }

    /**
     * sets the lower vertical movement limit of the camera
     * @param bottom the bottom limit in world space
     */
    public void setBottomLimit(float bottom) {
        // the position of the camera refers to the center of the screen, so add that offset to the limit
        bottomLimit = bottom + viewportHeight / 2.0f * Constants.WORLD_SCALE;
    }

    public void setHorizontalMovement(int movement) {
        horizontalMovement = movement;
    }

    public int getHorizontalMovement() {
        return horizontalMovement;
    }

    public void setVerticalMovement(int movement) {
        verticalMovement = movement;
    }

    public int getVerticalMovement() {
        return verticalMovement;
    }

    public void update(float delta) {
        if (cameraFocus != null)
            debugPosition.set(cameraFocus.getBody().getPosition());

        debugPosition.add(horizontalMovement * Constants.CAMERA_MOVE_VELOCITY * delta,
                verticalMovement * Constants.CAMERA_MOVE_VELOCITY * delta);

        // limit the vertical camera position so it does not go under the bottom limit
        debugPosition.y = Math.max(debugPosition.y, bottomLimit);
        position.set(Constants.getScreenSpaceVector(debugPosition));

        debugCamera.position.set(debugPosition, 0);
        camera.position.set(position, 0);

        debugCamera.update();
        camera.update();
    }

    public Matrix4 getDebugProjection() {
        return debugCamera.combined;
    }

    public Matrix4 getScreenProjection() {
        return camera.combined;
    }
}
