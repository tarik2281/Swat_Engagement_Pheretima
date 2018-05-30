package de.paluno.game.gameobjects.ground;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import de.paluno.game.Constants;
import de.paluno.game.GameState;
import de.paluno.game.gameobjects.PhysicsObject;
import de.paluno.game.gameobjects.Renderable;
import de.paluno.game.gameobjects.ShapeFactory;
import de.paluno.game.gameobjects.Updatable;
import de.paluno.game.screens.PlayScreen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

public class Ground implements PhysicsObject, Renderable, Updatable {

    private class IntPair implements Comparable<IntPair> {
        public int x;
        public int y;

        public IntPair() {
            this(0, 0);
        }

        public IntPair(int x, int y) {
            set(x, y);
        }

        public IntPair set(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }

        @Override
        public int compareTo(IntPair o) {
            int result = Integer.compare(x, o.x);

            if (result == 0)
                result = Integer.compare(y, o.y);

            return result;
        }
    }

    private Body body;
    private PlayScreen screen;

    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    private TiledMapTileLayer tileLayer;
    private ArrayList<IntPair> legalSpawnPositions;
    private TreeMap<IntPair, Fixture> fixtures;

    private LinkedList<Fixture> fixtureRemoveQueue;

    public Ground(PlayScreen screen, TiledMap tiledMap) {
        this.screen = screen;

        this.tiledMap = tiledMap;
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void initializeMap() {
        legalSpawnPositions = new ArrayList<IntPair>();
        fixtures = new TreeMap<IntPair, Fixture>();
        fixtureRemoveQueue = new LinkedList<Fixture>();

        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                initializeTileCollisions((TiledMapTileLayer)layer);
            }
        }
    }

    private void initializeTileCollisions(TiledMapTileLayer layer) {
        tileLayer = layer;

        float tileWidth = tileLayer.getTileWidth() * Constants.WORLD_SCALE;
        float tileHeight = tileLayer.getTileHeight() * Constants.WORLD_SCALE;

        Vector2 position = new Vector2();

        for (int column = 0; column < tileLayer.getWidth(); column++) {
            for (int row = 0; row < tileLayer.getHeight(); row++) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(column, row);

                if (cell != null && cell.getTile() != null) {
                    MapObjects objects = cell.getTile().getObjects();
                    if (objects != null) {
                        for (MapObject object : objects) {

                            Shape shape = null;

                            position.set(tileColumnToWorld(column), tileRowToWorld(row));

                            if (object instanceof RectangleMapObject) {
                                shape = ShapeFactory.createRectangleShape((RectangleMapObject)object, position);
                            }
                            else if (object instanceof PolygonMapObject) {
                                shape = ShapeFactory.createPolygonShape((PolygonMapObject)object, position);
                            }

                            if (shape != null) {
                                Fixture fixture = body.createFixture(shape, 0.0f);
                                fixture.setUserData("Ground");
                                fixtures.put(new IntPair(column, row), fixture);

                                float freeSpace = 0.0f;
                                boolean collision = false;

                                for (int i = 1; !collision && freeSpace < Constants.WORM_HEIGHT; i++) {
                                    if (checkTileCollision(column, row + i))
                                        collision = true;
                                    else
                                        freeSpace += tileHeight * Constants.WORLD_SCALE;
                                }

                                if (!collision)
                                    legalSpawnPositions.add(new IntPair(column, row + 1));
                            }
                        }
                    }
                }
            }
        }
    }

    private float tileColumnToWorld(int column) {
        return (column * tileLayer.getTileWidth() + tileLayer.getTileWidth() / 2.0f) * Constants.WORLD_SCALE;
    }

    private float tileRowToWorld(int row) {
        // TODO: worm height offset
        return (row * tileLayer.getTileHeight() + tileLayer.getTileHeight() / 2.0f) * Constants.WORLD_SCALE;
    }

    private boolean checkTileCollision(int x, int y) {
        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
        if (cell != null) {
            TiledMapTile tile = cell.getTile();
            if (tile != null) {
                MapObjects objects = tile.getObjects();
                if (objects != null)
                    return objects.getCount() > 0;
            }
        }

        return false;
    }

    public void destroyTerrain(Vector2 position, float radius) {
        Vector2 tilePosition = new Vector2(position).scl(1.0f / 16.0f);
        int tileRadius = Math.round(radius / 16.0f);
        int tileRadiusSQ = tileRadius * tileRadius;

        int tileX = Math.round(tilePosition.x);
        int tileY = Math.round(tilePosition.y);

        IntPair query = new IntPair(0, 0);
        for (int column = -tileRadius; column <= tileRadius; column++) {
            for (int row = -tileRadius; row <= tileRadius; row++) {
                if (column * column + row * row <= tileRadiusSQ) {
                    query.set(tileX + column, tileY + row);
                    Fixture fixture = fixtures.get(query);
                    if (fixture != null)
                        fixtureRemoveQueue.add(fixture);
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(tileX + column, tileY + row);
                    if (cell != null)
                        cell.setTile(null);
                }
            }
        }
    }

    public Vector2 getRandomSpawnPosition() {
        Random random = new Random();
        int index = random.nextInt(legalSpawnPositions.size());

        IntPair tile = legalSpawnPositions.remove(index);
        return new Vector2(tileColumnToWorld(tile.x), tileRowToWorld(tile.y));
    }

    @Override
    public void update(float delta, GameState gamestate) {
        for (Fixture fixture : fixtureRemoveQueue)
            body.destroyFixture(fixture);

        fixtureRemoveQueue.clear();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (mapRenderer == null)
            mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

        batch.end();
        mapRenderer.setView(screen.getCamera().getOrthoCamera());
        mapRenderer.render();
        batch.begin();
    }

    @Override
    public void setBodyToNullReference() {
        this.body = null;
    }

    @Override
    public void setupBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = screen.getWorld().createBody(bodyDef);

        initializeMap();
    }

    @Override
    public Body getBody() {
        return this.body;
    }
}
