package de.paluno.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Map {

    public static class Loader extends AsynchronousAssetLoader<Map, Loader.Parameter> {

        private TmxMapLoader tmxLoader;
        private Map map;

        public Loader(FileHandleResolver resolver) {
            super(resolver);

            tmxLoader = new TmxMapLoader(resolver);
        }

        @Override
        public void loadAsync(AssetManager manager, String fileName, FileHandle file, Parameter parameter) {
            tmxLoader.loadAsync(manager, fileName, file, null);
        }

        @Override
        public Map loadSync(AssetManager manager, String fileName, FileHandle file, Parameter parameter) {
            TiledMap tiledMap = tmxLoader.loadSync(manager, fileName, file, null);

            this.map = new Map();
            this.map.tiledMap = tiledMap;

            MapLayer tileLayer = tiledMap.getLayers().get(Constants.TILE_LAYER);
            if (tileLayer != null) {
                TiledMapTileLayer tileMapLayer = (TiledMapTileLayer)tileLayer;
                map.worldWidth = Constants.toUnits(tileMapLayer.getWidth() * tileMapLayer.getTileWidth());
                map.worldHeight = Constants.toUnits(tileMapLayer.getHeight() * tileMapLayer.getTileHeight());
            }

            MapLayer spawnLayer = tiledMap.getLayers().get(Constants.SPAWN_LAYER);
            if (spawnLayer != null) {
                int index = 0;
                map.spawnPoints = new Vector2[spawnLayer.getObjects().getCount()];
                for (MapObject object : spawnLayer.getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
                        map.spawnPoints[index++] = new Vector2(Constants.toUnits(rectangle.x),
                                Constants.toUnits(rectangle.y) + Constants.WORM_RADIUS);
                    }
                    else {
                        System.err.println("Invalid MapObject in SpawnPositions layer for file: " + fileName);
                    }
                }
            }
            else {
                System.err.println("Missing " + Constants.SPAWN_LAYER + " layer in file: " + fileName);
            }

            MapLayer collisionLayer = tiledMap.getLayers().get(Constants.COLLISION_LAYER);
            if (collisionLayer != null) {
                int index = 0;
                map.collisionObjects = new MapObject[collisionLayer.getObjects().getCount()];
                for (MapObject object : collisionLayer.getObjects()) {
                    map.collisionObjects[index++] = object;
                }
            }
            else {
                System.err.println("Missing " + Constants.COLLISION_LAYER + " in file: " + fileName);
            }

            return map;
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Parameter parameter) {
            return tmxLoader.getDependencies(fileName, file, null);
        }

        public static class Parameter extends AssetLoaderParameters<Map> {

        }
    }

    private TiledMap tiledMap;

    private float worldWidth;
    private float worldHeight;

    private Vector2[] spawnPoints;
    private MapObject[] collisionObjects;

    private Map() {

    }

    public Vector2[] getSpawnPoints() {
        return spawnPoints;
    }

    public MapObject[] getCollisionObjects() {
        return collisionObjects;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }
}
