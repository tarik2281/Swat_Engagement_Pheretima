package de.paluno.game.interfaces;

public class ShootEvent extends GameEvent {

    public int weaponType;
    public ProjectileData[] projectiles;

    public ShootEvent() {

    }

    public ShootEvent(int tick, int weaponType, ProjectileData[] projectiles) {
        super(tick, Type.SHOOT);

        this.weaponType = weaponType;
        this.projectiles = projectiles;
    }
}
