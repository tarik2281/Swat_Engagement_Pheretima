package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import de.paluno.game.Constants;
import de.paluno.game.gameobjects.Explosion;

public class ExplosionMaskRenderer implements Disposable {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private Ground ground;

    public ExplosionMaskRenderer(OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();

        this.ground = null;
        this.camera = camera;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    public void setGround(Ground ground) {
        this.ground = ground;
    }

    public void renderDepthMask() {
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
        Gdx.gl.glClearDepthf(0.0f);
        Gdx.gl.glDepthFunc(GL20.GL_GREATER);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);

        camera.position.z = 1.0f;
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        for (Explosion explosion : ground.getExplosions()) {
            shapeRenderer.circle(explosion.getCenter().x * Constants.SCREEN_SCALE,
                    explosion.getCenter().y * Constants.SCREEN_SCALE,
                    explosion.getRadius() * Constants.SCREEN_SCALE, explosion.getNumSegments());
        }

        shapeRenderer.end();

        camera.position.z = 0.0f;
        camera.update();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(false);
        Gdx.gl.glColorMask(true, true, true, true);
    }

    public void enableMask() {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }

    public void disableMask() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }
}
