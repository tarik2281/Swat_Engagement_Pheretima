package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Align;

import de.paluno.game.Constants;

public class CratePickupText implements Renderable {
	
	private World world;
	private Worm worm;
	
	private BitmapFont font;
    private GlyphLayout layout;
    
    private float opacity = 1;
    private float offset = 0;
    private boolean fade = false;
	
	public CratePickupText(World world, Worm target, String content) {
		this.world = world;
        this.worm = target;

        font = new BitmapFont();
        // the text moves shaky if we use integer positions
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();
        
        this.setText(content);
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		Body body = worm.getBody();
        if (body == null) {
            // the worm associated with this HealthBar does not exist anymore so just remove this object from the game
            world.forgetAfterUpdate(this);
            return;
        }

        Vector2 position = Constants.getScreenSpaceVector(body.getPosition());
        
        if(fade) {
        	this.opacity -= 1 * delta;
        	if(opacity <= 0) opacity = 0;
        	if(opacity == 0) world.forgetAfterUpdate(this);
        }
        this.setColorRGBA(getColor(), opacity);
        
        //Pos.y + fixed Offset (no overlapping) + dynamic offset (shifting upwards)
        font.draw(batch, layout, position.x, position.y + (50 + offset));
        
        offset += 5 * delta;
        if(offset >= 5) this.fade = true;
	}
	
	private void setText(String text) {
		layout.setText(font, "+1 "+text, getColor(), 0, Align.center, false);
	}
	
	private void setColorRGBA(Color color, float alpha) {
		this.font.setColor(color.r, color.g, color.b, alpha);
	}
	
	private Color getColor() {
		Color color = Constants.PLAYER_COLORS[worm.getPlayerNumber()];

        return color;
	}

}
