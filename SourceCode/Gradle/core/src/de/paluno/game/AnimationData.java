package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class AnimationData {

    private String spriteSheetPath;
    private Texture spriteSheet;
    private int numColumns;
    private int numRows;
    private float frameInterval;
    private float spriteOffset;
    private Animation.PlayMode playMode;

    private int frameWidth;
    private int frameHeight;

    private AnimationData(FileHandle fileHandle) {
        readXml(fileHandle);
    }

    public float getSpriteOffset() {
        return spriteOffset;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public Animation.PlayMode getPlayMode() {
        return playMode;
    }

    private void readXml(FileHandle fileHandle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(fileHandle);

        // read animation data from xml file
        spriteSheetPath = root.get("Texture");
        numColumns = root.getInt("Columns");
        numRows = root.getInt("Rows");
        frameInterval = root.getFloat("FrameInterval");
        spriteOffset = root.getFloat("SpriteOffset");
        String playMode = root.get("PlayMode");

        this.playMode = Animation.PlayMode.NORMAL;

        if (playMode.equals("Loop"))
            this.playMode = Animation.PlayMode.LOOP;
        else if (playMode.equals("LoopPingPong"))
            this.playMode = Animation.PlayMode.LOOP_PINGPONG;
    }

    private void setupSpriteSheet(Texture texture) {
        spriteSheet = texture;

        // calculate the frame size based on the size of the sprite sheet and its structure
        frameWidth = texture.getWidth() / numColumns;
        frameHeight = texture.getHeight() / numRows;
    }

    public Animation<TextureRegion> generateAnimation() {
        // generate TextureRegions for each frame in the sprite sheet
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // convert two-dimensional array to one-dimensional array
        TextureRegion[] frames = new TextureRegion[numColumns * numRows];
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        // create libGDX animation with the TextureRegions as frames
        Animation<TextureRegion> animation = new Animation<TextureRegion>(frameInterval, frames);
        animation.setPlayMode(playMode);

        return animation;
    }

    public static class Loader extends SynchronousAssetLoader<AnimationData, Loader.Parameter> {

        private AnimationData animationData;

        /**
         * Constructor, sets the {@link FileHandleResolver} to use to resolve the file associated with the asset name.
         *
         * @param resolver
         */
        public Loader(FileHandleResolver resolver) {
            super(resolver);
        }

        @Override
        public AnimationData load(AssetManager assetManager, String fileName, FileHandle file, Parameter parameter) {
            animationData.setupSpriteSheet(assetManager.get(animationData.spriteSheetPath, Texture.class));

            return animationData;
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Parameter parameter) {
            Array<AssetDescriptor> deps = new Array<AssetDescriptor>(1);

            animationData = new AnimationData(file);

            AssetDescriptor<Texture> spriteSheet = new AssetDescriptor<Texture>(animationData.spriteSheetPath, Texture.class);
            deps.add(spriteSheet);

            return deps;
        }

        public static class Parameter extends AssetLoaderParameters<AnimationData> {

        }
    }
}
