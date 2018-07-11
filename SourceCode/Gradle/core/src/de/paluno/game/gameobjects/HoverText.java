package de.paluno.game.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import de.paluno.game.Constants;

public class HoverText extends WorldObject {

	private String text;

	private BitmapFont font;
    private GlyphLayout layout;
    private Color color;
    
    private float opacity = 1;
    private float offset = 0;
    private boolean fade = false;
	
	public HoverText(WorldObject target, String text, Color color) {
		setPosition(target.getPosition());
        this.color = color;
        this.text = text;

        font = new BitmapFont();
        // the text moves shaky if we use integer positions
        font.setUseIntegerPositions(false);

        layout = new GlyphLayout();
        
        this.setText(text);
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
        Vector2 position = Constants.getScreenSpaceVector(getPosition());
        
        if(fade) {
        	this.opacity -= 1 * delta;
        	if(opacity <= 0) {
				opacity = 0;
				removeFromWorld();
			}
        }
        this.setColorRGBA(getColor(), opacity);
        
        //Pos.y + fixed Offset (no overlapping) + dynamic offset (shifting upwards)
		font.draw(batch, text, position.x, position.y + (50 + offset), 0, Align.center, false);
        
        offset += 5 * delta;
        if(offset >= 5) this.fade = true;
	}
	
	private void setText(String text) {
		layout.setText(font, text, getColor(), 0, Align.center, false);
	}
	
	private void setColorRGBA(Color color, float alpha) {
		this.font.setColor(color.r, color.g, color.b, alpha);
	}
	
	private Color getColor() {return color;}
	
	public void updateText(String text) {
		this.setText(text);
		this.setColorRGBA(color, opacity);
	}

}
