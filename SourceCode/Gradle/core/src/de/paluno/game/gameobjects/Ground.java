package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import de.paluno.game.Constants;
import de.paluno.game.screens.PlayScreen;

public class Ground implements PhysicsObject, Renderable {

    private Body body;
    private PlayScreen screen;
    private Texture texture;
    private TextureRegion textureRegion;
    private Sprite sprite;

    public Ground(PlayScreen screen){
        this.screen = screen;

        texture = new Texture(Gdx.files.internal("Stone-0233.jpg"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        textureRegion = new TextureRegion(texture);
        // TODO: texture disposal
        textureRegion.setRegion(0, 0, (int)(Constants.GROUND_WIDTH * Constants.SCREEN_SCALE),
                (int)(Constants.GROUND_HEIGHT * Constants.SCREEN_SCALE));

        sprite = new Sprite(textureRegion);
        sprite.setOriginCenter();
        sprite.setOriginBasedPosition(0, 0);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        sprite.draw(batch);
    }

    @Override
    public void setBodyToNullReference() {
        this.body = null;
    }

    @Override
    public void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.GROUND_WIDTH / 2,
                Constants.GROUND_HEIGHT / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        this.body = screen.getWorld().createBody(bodyDef);
        
        Fixture fix = body.createFixture(fixtureDef);
        // CollisionHandler Identifier
        fix.setUserData("Ground");

        shape.dispose();
    }

    @Override
    public Body getBody() {
        return this.body;
    }
}
