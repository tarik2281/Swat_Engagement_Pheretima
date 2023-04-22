package de.paluno.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import de.paluno.game.gameobjects.WorldObject;


public class GameCamera {
	

    // normal camera used for drawing sprites, etc., metrics given in pixels
    private OrthographicCamera camera;
    // debug camera used for drawing hitboxes, metrics given in meters
    private OrthographicCamera debugCamera;
    private WorldObject cameraFocus;

    private float viewportWidth;
    private float viewportHeight;

    private Vector2 debugPosition;
    private Vector2 position;

    private int horizontalMovement;
    private int verticalMovement;

    private Rectangle worldBounds;
    private Vector2 goToPoint;
    // in world space (in meters)
    private float bottomLimit;
    private float zoom;

    private Vector2 focusDirectionVector = new Vector2();

    /**
     *
     * @param viewportWidth in pixels
     * @param viewportHeight in pixels
     */
    public GameCamera(float viewportWidth, float viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        // initialize the cameras according to the window size
        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, viewportWidth * Constants.WORLD_SCALE,
                viewportHeight * Constants.WORLD_SCALE);

        debugPosition = new Vector2();
        position = new Vector2();

        zoom = 1.0f;
    }

    public void updateViewport(float width, float height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;

        debugCamera.viewportWidth = width * Constants.WORLD_SCALE;
        debugCamera.viewportHeight = height * Constants.WORLD_SCALE;

        setZoom(zoom);
    }

    // set the object the camera will follow, or null to follow nothing
    public void setCameraFocus(WorldObject object) {
        cameraFocus = object;
        goToPoint = null;
    }

    public WorldObject getCameraFocus() {
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

    public void setWorldBounds(Rectangle worldBounds) {
        this.worldBounds = worldBounds;
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
        Vector2 focusPoint = goToPoint;
        if (cameraFocus != null)
            focusPoint = cameraFocus.getPosition();

        if (focusPoint != null) {
            // smooth following the object
            focusDirectionVector.set(focusPoint).add(-debugPosition.x, -debugPosition.y);
            focusDirectionVector.nor();
            float distance = focusPoint.dst(debugPosition);
            focusDirectionVector.scl(distance / Constants.CAMERA_FOCUS_TIME * delta);
            debugPosition.add(focusDirectionVector);
        }

        if (cameraFocus != null && cameraFocus.getBody() != null) {
            // smooth following the object
            focusDirectionVector.set(cameraFocus.getBody().getPosition()).add(-debugPosition.x, -debugPosition.y);
            focusDirectionVector.nor();
            float distance = cameraFocus.getBody().getPosition().dst(debugPosition);
            focusDirectionVector.scl(distance / Constants.CAMERA_FOCUS_TIME * delta);
            debugPosition.add(focusDirectionVector);

            // if we have a camera focus then we center the camera on the given object
            //debugPosition.set(cameraFocus.getBody().getPosition());
        }

        // move the camera according to user input
        debugPosition.add(horizontalMovement * Constants.CAMERA_MOVE_VELOCITY * delta,
                verticalMovement * Constants.CAMERA_MOVE_VELOCITY * delta);

        debugPosition.x = MathUtils.clamp(debugPosition.x, worldBounds.x + debugCamera.viewportWidth * zoom / 2.0f, worldBounds.x + worldBounds.width - debugCamera.viewportWidth * zoom / 2.0f);
        debugPosition.y = MathUtils.clamp(debugPosition.y, worldBounds.y + debugCamera.viewportHeight * zoom / 2.0f, worldBounds.y + worldBounds.height - debugCamera.viewportHeight * zoom / 2.0f);
        // limit the vertical camera position so it does not go under the bottom limit
        //debugPosition.y = Math.max(debugPosition.y, bottomLimit);

        // since we calculated the camera position in meters for debug drawing, convert that to pixels for normal drawing
        position.set(Constants.getScreenSpaceVector(debugPosition));

        // apply the new camera position to the camera objects
        debugCamera.position.set(debugPosition, 0);
        camera.position.set(position, 0);

        // let the Camera class calculate its new projection matrices
        debugCamera.update();
        camera.update();
    }

    public void setPositionToFocus() {
        if (cameraFocus != null)
            setCameraPosition(cameraFocus.getPosition());
    }
    
    public void setCameraPosition(Vector2 position) {
    	debugPosition.set(position);
    	goToPoint = null;
    }

    public void goToPoint(float x, float y) {
        goToPoint = new Vector2(x, y);
        cameraFocus = null;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;

        if (this.zoom <= 0.1f)
            this.zoom = 0.1f;

        float zoomWidth = worldBounds.width / debugCamera.viewportWidth;
        float zoomHeight = worldBounds.height / debugCamera.viewportHeight;

        this.zoom = Math.min(this.zoom, zoomWidth);
        this.zoom = Math.min(this.zoom, zoomHeight);

        camera.zoom = this.zoom;
        debugCamera.zoom = this.zoom;
    }

    public Matrix4 getDebugProjection() {
        return debugCamera.combined;
    }

    public Matrix4 getScreenProjection() {
        // return the camera matrix to be used with a SpriteBatch
        return camera.combined;
    }

    public Vector2 getWorldPosition() {
        return debugPosition;
    }

    public OrthographicCamera getOrthoCamera() {
        return camera;
    }

    public OrthographicCamera getWorldCamera() {
        return debugCamera;
    }
   
}
