package de.paluno.game.interfaces;

public class ShootEvent extends GameEvent {

    public ProjectileData[] projectiles;

    public ShootEvent() {

    }

    public ShootEvent(int tick, ProjectileData[] projectiles) {
        super(tick, Type.SHOOT);

        this.projectiles = projectiles;
    }
}
