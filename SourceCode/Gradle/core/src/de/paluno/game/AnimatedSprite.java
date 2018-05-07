package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;

public class AnimatedSprite {

    private Texture spriteSheet;
    private int numColumns;
    private int numRows;
    private float frameInterval;
    private float spriteOffset;
    private Animation.PlayMode playMode;

    private Animation<TextureRegion> animation;
    private int tileHalfWidth;
    private int tileHalfHeight;

    private float stateTime;
    private boolean reverse;

    private Vector2 origin;
    private boolean flipX;

    public AnimatedSprite(FileHandle fileHandle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element element = reader.parse(fileHandle);

        spriteSheet = new Texture(Gdx.files.internal(element.get("Texture")));
        numColumns = element.getInt("Columns");
        numRows = element.getInt("Rows");
        frameInterval = element.getFloat("FrameInterval");
        spriteOffset = element.getFloat("SpriteOffset");
        String playMode = element.get("PlayMode");

        this.playMode = Animation.PlayMode.NORMAL;
        if (playMode.equals("Loop"))
            this.playMode = Animation.PlayMode.LOOP;
        else if (playMode.equals("LoopPingPong"))
            this.playMode = Animation.PlayMode.LOOP_PINGPONG;

        tileHalfWidth = spriteSheet.getWidth() / numColumns;
        tileHalfHeight = spriteSheet.getHeight() / numRows;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, tileHalfWidth, tileHalfHeight);

        tileHalfWidth /= 2;
        tileHalfHeight /= 2;

        TextureRegion[] frames = new TextureRegion[numColumns * numRows];
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        animation = new Animation<TextureRegion>(frameInterval, frames);
        animation.setPlayMode(this.playMode);

        stateTime = 0.0f;

        origin = new Vector2();
    }

    public void setPosition(Vector2 position) {
        origin.set(position);
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    /**
     * reset the animation to the first frame
     */
    public void reset() {
        stateTime = 0;
        reverse = false;
    }

    public void reverse() {
        reverse = true;
    }

    public boolean isAnimationFinished() {
        if (reverse)
            return stateTime <= 0;
        else
            return stateTime >= animation.getAnimationDuration();
    }

    public void draw(SpriteBatch batch, float delta) {
        stateTime += !reverse ? delta : -delta;
        // create loop effect by resetting stateTime
        switch (playMode) {
            case NORMAL:
                if (!reverse && stateTime >= animation.getAnimationDuration())
                    stateTime = animation.getAnimationDuration();
                else if (reverse && stateTime <= 0)
                    stateTime = 0;
                break;
            case LOOP:
                if (stateTime >= animation.getAnimationDuration())
                    stateTime -= animation.getAnimationDuration();
                break;
            case LOOP_PINGPONG:
                if (stateTime >= animation.getAnimationDuration() * 2)
                    stateTime -= animation.getAnimationDuration() * 2;
                break;
        }

        // sprite sheets already involve worm movement offset, so remove that from there
        float pct = stateTime / animation.getAnimationDuration();
        float offset = spriteOffset * pct;
        if (flipX)
            offset *= -1.0f;

        TextureRegion region = animation.getKeyFrame(stateTime, playMode != Animation.PlayMode.NORMAL);

        if (flipX && !region.isFlipX())
            region.flip(true, false);
        else if (!flipX && region.isFlipX())
            region.flip(true, false);

        batch.draw(region, origin.x - tileHalfWidth + offset, origin.y - tileHalfHeight);
    }
}
