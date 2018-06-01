package de.paluno.game.gameobjects.ground;

import de.lighti.clipper.*;

class ClipperWrapper {
    private static final float PRECISION_SCALE = 10000.0f;

    private Clipper clipper;
    private Paths result;

    ClipperWrapper() {
        clipper = new DefaultClipper();
        result = new Paths();
    }

    void addSubjectPolygon(float[] vertices) {
        clipper.addPath(verticesToPath(vertices), Clipper.PolyType.SUBJECT, true);
    }

    void addClipPolygon(float[] vertices) {
        clipper.addPath(verticesToPath(vertices), Clipper.PolyType.CLIP, true);
    }

    void clearPolygons() {
        clipper.clear();
    }

    private Path verticesToPath(float[] vertices) {
        Path path = new Path();

        for (int i = 0; i < vertices.length; i++) {
            path.add(new Point.LongPoint(getLong(vertices[i]),
                    getLong(vertices[++i])));
        }

        return path;
    }

    private float[] pathToVertices(Path path) {
        float[] vertices = new float[path.size() * 2];

        int index = 0;
        for (Point.LongPoint point : path) {
            vertices[index++] = getFloat(point.getX());
            vertices[index++] = getFloat(point.getY());
        }

        return vertices;
    }

    private long getLong(float f) {
        return (long)(f * PRECISION_SCALE);
    }

    private float getFloat(long l) {
        return l / PRECISION_SCALE;
    }

    float[][] clip() {
        result.clear();

        clipper.execute(Clipper.ClipType.DIFFERENCE, result);
        float[][] res = new float[result.size()][];
        int index = 0;
        for (Path path : result) {
            res[index++] = pathToVertices(path);
        }

        clipper.clear();
        result.clear();
        return res;
    }
}
