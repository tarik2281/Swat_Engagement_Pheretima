package de.paluno.game.worldhandlers;

import com.badlogic.gdx.math.Vector2;
import de.paluno.game.Constants;
import de.paluno.game.EventManager;
import de.paluno.game.gameobjects.*;
import de.paluno.game.interfaces.*;
import de.paluno.game.screens.PlayScreen;

import java.util.Iterator;
import java.util.List;

public abstract class InterpolationWorldHandler extends WorldHandler {

    private List<GameEvent> pendingEvents;
    private List<WorldData> pendingSnapshots;

    private float timeShift;
    private float currentTime;

    private WorldData currentSnapshot;
    private WorldData nextSnapshot;
    private PhysicsData physicsCache = new PhysicsData();

    protected abstract boolean shouldInterpolate();
    protected void onGameDataProcessed(GameData gameData) {}

    public InterpolationWorldHandler(PlayScreen screen, int mapNumber) {
        super(screen, mapNumber);
    }

    public void setTimeShift(float timeShift) {
        this.timeShift = timeShift;
    }

    public void setCurrentTime(float time) {
        currentTime = time;
    }

    public void setEventList(List<GameEvent> eventList) {
        pendingEvents = eventList;
    }

    public void setSnapshotList(List<WorldData> snapshotList) {
        pendingSnapshots = snapshotList;
    }

    public void clearSnapshots() {
        currentSnapshot = null;
        nextSnapshot = null;
        pendingSnapshots.clear();
    }

    @Override
    protected void onUpdate(float delta) {
        if (!isIdle())
            currentTime += delta;

        if (shouldInterpolate()) {
            GameEvent currentEvent;
            while ((currentEvent = pollEvents()) != null) {
                switch (currentEvent.getType()) {
                    case EXPLOSION: {
                        ExplosionEvent ex = (ExplosionEvent) currentEvent;
                        Projectile projectile = getProjectileById(ex.projectileId);
                        getWorld().addExplosion(new Explosion(new Vector2(ex.getCenterX(), ex.getCenterY()),
                                ex.getRadius(), ex.getBlastPower()));
                        EventManager.getInstance().queueEvent(EventManager.Type.ProjectileExploded, projectile);
                        break;
                    }
                    case SHOOT: {
                        for (ProjectileData data : ((ShootEvent) currentEvent).projectiles) {
                            Projectile projectile = new Projectile(null, WeaponType.values()[data.getType()],
                                    new Vector2(data.getPhysicsData().getPositionX(), data.getPhysicsData().getPositionY()), new Vector2());
                            addProjectile(projectile);
                            projectile.setId(data.getId());
                        }
                        EventManager.getInstance().queueEvent(EventManager.Type.WeaponShoot, getCurrentPlayer().getCurrentWeapon());
                        break;
                    }
                    case END_TURN: {
                        setIdle();
                        break;
                    }
                    case WORM_DIED: {
                        WormEvent event = (WormEvent) currentEvent;
                        Player player = getPlayers().get(event.getPlayerNumber());
                        Worm worm = player.getWormByNumber(event.getWormNumber());
                        worm.die(Constants.DEATH_TYPE_NO_HEALTH);
                        break;
                    }
                    case WORM_FELL_DOWN: {
                        WormEvent event = (WormEvent) currentEvent;
                        Player player = getPlayers().get(event.getPlayerNumber());
                        Worm worm = player.getWormByNumber(event.getWormNumber());
                        worm.die(Constants.DEATH_TYPE_FALL_DOWN);
                        break;
                    }
                    case WORM_INFECTED: {
                        WormEvent event = (WormEvent) currentEvent;
                        Player player = getPlayers().get(event.getPlayerNumber());
                        Worm worm = player.getWormByNumber(event.getWormNumber());
                        worm.setIsInfected(true);
                        break;
                    }
                    case WORM_TOOK_DAMAGE: {
                        WormDamageEvent event = (WormDamageEvent) currentEvent;
                        Player player = getPlayers().get(event.getPlayerNumber());
                        Worm worm = player.getWormByNumber(event.getWormNumber());
                        worm.takeDamage(event.getDamage(), event.getDamageType());
                        break;
                    }
                    case AIR_BALL:
                        EventManager.getInstance().queueEvent(EventManager.Type.AirBall, null);
                        break;
                    case HEADSHOT:
                        EventManager.getInstance().queueEvent(EventManager.Type.Headshot, null);
                        break;
                    case GRENADE_COLLISION:
                        EventManager.getInstance().queueEvent(EventManager.Type.GrenadeCollision, null);
                        break;
                    case FEET_COLLISION:
                        EventManager.getInstance().queueEvent(EventManager.Type.FeetCollision, null);
                        break;
                }
                onGameDataProcessed(currentEvent);
            }

            interpolateWorldSnapshots();
        }
    }

    private float getSnapshotsRatio(float shiftedTime) {
        float total = nextSnapshot.getReceivingTimeStamp() - currentSnapshot.getReceivingTimeStamp();

        return Math.min(1.0f, (shiftedTime - currentSnapshot.getReceivingTimeStamp()) / total);
    }

    private void interpolateWorldSnapshots() {
        float shiftedTime = updateCurrentSnapshots();

        if (currentSnapshot != null) {
            float to = 0.0f;

            if (nextSnapshot != null)
                to = getSnapshotsRatio(shiftedTime);

            float from = 1.0f - to;

            int currentWeapon = currentSnapshot.getCurrentWeapon();
            if (currentWeapon != -1) {
                Worm currentWorm = getCurrentPlayer().getCurrentWorm();

                if (currentWorm.getCurrentWeapon() != null && currentWorm.getCurrentWeapon().getWeaponType().ordinal() != currentWeapon)
                    equipWeapon(WeaponType.values()[currentWeapon]);
            }

            Object fromData = currentSnapshot.getIndicatorData();
            Object toData = null;
            if (nextSnapshot != null)
                toData = nextSnapshot.getIndicatorData();

            if (getWeaponIndicator() != null) {
                if (fromData != null && toData != null && toData.getClass() != fromData.getClass())
                    toData = null;

                getWeaponIndicator().interpolateSnapshots(fromData, toData, to);
            }

            for (Projectile projectile : getProjectiles()) {
                ProjectileData currentData = currentSnapshot.getProjectileById(projectile.getId());
                if (currentData != null) {
                    PhysicsData physics = null;

                    if (nextSnapshot != null) {
                        ProjectileData nextData = nextSnapshot.getProjectileById(projectile.getId());
                        if (nextData != null)
                            physics = nextData.getPhysicsData();
                    }

                    physics = physicsCache.interpolate(currentData.getPhysicsData(), physics, to);
                    projectile.setPhysics(physics);
                }
            }

            for (PlayerData playerData : currentSnapshot.getPlayers()) {
                Player player = getPlayers().get(playerData.getPlayerNumber());

                for (WormData wormData : playerData.getWorms()) {
                    Worm worm = player.getWormByNumber(wormData.getWormNumber());

                    if (worm != null && !worm.isDead()) {
                        PhysicsData physics = null;

                        if (nextSnapshot != null) {
                            physics = nextSnapshot.getPlayer(player.getPlayerNumber()).getWormByNumber(wormData.getWormNumber()).getPhysicsData();
                        }

                        physics = physicsCache.interpolate(wormData.getPhysicsData(), physics, to);
                        worm.setPhysics(physics);
                        worm.setMovement(wormData.getMovement());
                        worm.setNumContacts(wormData.getNumGroundContacts());
                    }
                }
            }
        }
        else {
            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer != null) {
                Worm currentWorm = currentPlayer.getCurrentWorm();
                if (currentWorm != null)
                    currentWorm.setNumContacts(1);
            }
        }
    }

    private float updateCurrentSnapshots() {
        float shiftedTime = currentTime + timeShift;

        if (nextSnapshot != null) {
            if (shiftedTime <= nextSnapshot.getReceivingTimeStamp())
                return shiftedTime;

            if (currentSnapshot != null)
                onGameDataProcessed(currentSnapshot);
            currentSnapshot = nextSnapshot;
            nextSnapshot = null;
        }

        for (Iterator<WorldData> it = pendingSnapshots.iterator(); it.hasNext(); ) {
            WorldData gameData = it.next();
            it.remove();

            if (gameData == null)
                continue;

            if (shiftedTime <= gameData.getReceivingTimeStamp()) {
                nextSnapshot = gameData;
                break;
            }
            else {
                if (currentSnapshot != null)
                    onGameDataProcessed(currentSnapshot);

                currentSnapshot = gameData;
            }
        }

        return shiftedTime;
    }

    private GameEvent pollEvents() {
        float shiftedTime = currentTime + timeShift;

        for (Iterator<GameEvent> it = pendingEvents.iterator(); it.hasNext(); ) {
            GameEvent event = it.next();
            if (shiftedTime >= event.getReceivingTimeStamp()) {
                it.remove();
                return event;
            }
        }

        return null;
    }
}
