package de.paluno.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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

    public Ground(PlayScreen screen){
        this.screen = screen;

        texture = new Texture(Gdx.files.internal("Stone-0233.jpg"));
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        textureRegion = new TextureRegion(texture);
        // TODO: ground width and height
        textureRegion.setRegion(0, 0, Constants.GROUND_WIDTH, Constants.GROUND_HEIGHT);
        
    
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(textureRegion, 0, 0);
    }

    @Override
    public void setBodyToNullReference() {
        this.body = null;
    }

    @Override
    public void setupBody() {
        // TODO Auto-generated method stub

        Vector2 position = Constants.getWorldSpaceVector(new Vector2(Constants.GROUND_WIDTH / 2.0f, Constants.GROUND_HEIGHT / 2.0f));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(position.x, position.y);

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
