package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
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
