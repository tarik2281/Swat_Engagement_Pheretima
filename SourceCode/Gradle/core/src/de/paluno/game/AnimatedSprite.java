package de.paluno.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AnimatedSprite {

    private AnimationData animationData;

    private Animation<TextureRegion> animation;
    private int frameHalfWidth;
    private int frameHalfHeight;

    private float stateTime;
    private boolean reverse;

    private Vector2 position;
    private boolean flipHorizontal;

    public AnimatedSprite(AnimationData data) {
        animationData = data;

        stateTime = 0.0f;
        reverse = false;
        position = new Vector2();

        frameHalfWidth = animationData.getFrameWidth() / 2;
        frameHalfHeight = animationData.getFrameHeight() / 2;

        animation = animationData.generateAnimation();
    }

    public AnimationData getAnimationData() {
        return animationData;
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
        stateTime += animationData.getPlayMode() != Animation.PlayMode.NORMAL || !reverse ? delta : -delta;
        // create loop effect by resetting stateTime
        switch (animationData.getPlayMode()) {
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
        float offset = animationData.getSpriteOffset() * pct;
        if (flipHorizontal)
            offset *= -1.0f;

        TextureRegion region = animation.getKeyFrame(stateTime, animationData.getPlayMode() != Animation.PlayMode.NORMAL);

        if (flipHorizontal && !region.isFlipX())
            region.flip(true, false);
        else if (!flipHorizontal && region.isFlipX())
            region.flip(true, false);

        // we have the origin in the center of the object so subtract half of the frame size
        batch.draw(region, position.x - frameHalfWidth + offset, position.y - frameHalfHeight);
    }
}
