package de.paluno.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader;

public class AnimatedSprite implements Disposable {

    private Texture spriteSheet;
    private int numColumns;
    private int numRows;
    private float frameInterval;
    private float spriteOffset;
    private Animation.PlayMode playMode;

    private Animation<TextureRegion> animation;
    private int frameHalfWidth;
    private int frameHalfHeight;

    private float stateTime;
    private boolean reverse;

    private Vector2 position;
    private boolean flipHorizontal;

    public AnimatedSprite(FileHandle fileHandle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element element = reader.parse(fileHandle);

        // read animation data from xml file
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

        // calculate the frame size based on the size of the sprite sheet and its structure
        int frameWidth = spriteSheet.getWidth() / numColumns;
        int frameHeight = spriteSheet.getHeight() / numRows;

        // generate TextureRegions for each frame in the sprite sheet
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        frameHalfWidth = frameWidth / 2;
        frameHalfHeight = frameHeight / 2;

        // convert two-dimensional array to one-dimensional array
        TextureRegion[] frames = new TextureRegion[numColumns * numRows];
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // create libGDX animation with the TextureRegions as frames
        animation = new Animation<TextureRegion>(frameInterval, frames);
        animation.setPlayMode(this.playMode);

        stateTime = 0.0f;

        position = new Vector2();
    }

    /**
     * sets the position where this sprite will be drawn
     * @param position
     */
    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void setFlipHorizontal(boolean flip) {
        this.flipHorizontal = flip;
    }

    /**
     * reset the animation to the first frame
     */
    public void reset() {
        stateTime = 0;
        reverse = false;
    }

    /**
     * play the animation backwards - only usable with a PlayMode.NORMAL animation
     */
    public void reverse() {
        reverse = true;
        stateTime = animation.getAnimationDuration();
    }

    /**
     * checks if this animation has finished playing
     * @return true if the animation finished, otherwise false
     */
    public boolean isAnimationFinished() {
        if (reverse)
            return stateTime <= 0;
        else
            return stateTime >= animation.getAnimationDuration();
    }

    public void draw(SpriteBatch batch, float delta) {
        stateTime += playMode != Animation.PlayMode.NORMAL || !reverse ? delta : -delta;
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
        if (flipHorizontal)
            offset *= -1.0f;

        TextureRegion region = animation.getKeyFrame(stateTime, playMode != Animation.PlayMode.NORMAL);

        if (flipHorizontal && !region.isFlipX())
            region.flip(true, false);
        else if (!flipHorizontal && region.isFlipX())
            region.flip(true, false);

        // we have the origin in the center of the object so subtract half of the frame size
        batch.draw(region, position.x - frameHalfWidth + offset, position.y - frameHalfHeight);
    }

    @Override
    public void dispose() {
        if (spriteSheet != null) {
            spriteSheet.dispose();
            spriteSheet = null;
        }
    }
}
