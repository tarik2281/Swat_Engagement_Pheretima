package de.paluno.game.gameobjects;

public abstract class WeaponIndicator<T> extends WorldObject {

    public enum Type {
        ShotDirection, Pointer;

        public WeaponIndicator newInstance() {
            switch (this) {
                case ShotDirection:
                    return new ShotDirectionIndicator();
            }

            return null;
        }
    }

    public abstract T makeSnapshot();
    public abstract void interpolateSnapshots(T from, T to, float ratio);
    public abstract Type getType();
    public abstract boolean isCameraMovable();
}
