package de.paluno.game.gameobjects;

public abstract class WeaponIndicator<T> extends WorldObject {

    public enum Type {
        ShotDirection, Pointer, None;

        public WeaponIndicator newInstance() {
            switch (this) {
                case ShotDirection:
                    return new ShotDirectionIndicator();
                case Pointer:
                	return new AirstrikeIndicator();
            }

            return null;
        }
    }

    public abstract T makeSnapshot();
    public abstract void interpolateSnapshots(T from, T to, float ratio);
    public abstract Type getType();
}
