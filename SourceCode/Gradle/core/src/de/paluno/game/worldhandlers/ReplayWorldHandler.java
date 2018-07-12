package de.paluno.game.worldhandlers;

import de.paluno.game.EventManager;
import de.paluno.game.GameState;
import de.paluno.game.gameobjects.*;
import de.paluno.game.screens.PlayScreen;

public class ReplayWorldHandler extends InterpolationWorldHandler {

    private Replay replay;
    private boolean replayPlaying = false;

    public ReplayWorldHandler(PlayScreen screen, Replay replay) {
        super(screen, replay.getMapNumber());

        this.replay = replay;
    }

    @Override
    protected boolean shouldInterpolate() {
        return true;
    }

    @Override
    protected void onInitializePlayers() {
        setEventList(replay.getGameEvents());
        setSnapshotList(replay.getWorldSnapshots());

        getWorld().setFromSnapshot(replay.getSetupSnapshot().getWorldSnapshot());
        getWorld().getCamera().setCameraPosition(replay.getCameraPosition());

        for (Player.SnapshotData playerData : replay.getSetupSnapshot().getPlayerSnapshots()) {
            Player player = addPlayer(playerData.getPlayerNumber());

            for (Worm.SnapshotData wormData : playerData.wormData) {
                Worm worm = addWorm(player, wormData.characterNumber, "");
                worm.setFromSnapshot(wormData);
                if (worm.isDead())
                    getWorld().forgetAfterUpdate(worm);
            }
        }

        for (Projectile.SnapshotData projectileData : replay.getSetupSnapshot().getProjectileSnapshots()) {
            addProjectile(projectileData);
        }

        for (Projectile.SnapshotData projectileData : replay.getSetupSnapshot().getTurretSnapshots()) {
            addProjectile(projectileData);
        }

        for (AirdropCrate.SnapshotData crateData : replay.getSetupSnapshot().getCrateSnapshots()) {
            addCrate(new AirdropCrate(crateData));
        }
    }

    @Override
    protected boolean shouldAcceptInput() {
        return false;
    }

    @Override
    protected boolean shouldWorldStep() {
        return getCurrentGameState() == GameState.NONE || getCurrentGameState() == GameState.IDLE;
    }

    @Override
    protected boolean shouldCreateReplay() {
        return false;
    }

    @Override
    protected boolean shouldStartInstantly() {
        return true;
    }

    @Override
    protected void requestNextTurn() {
        if (replayPlaying) {
            EventManager.getInstance().queueEvent(EventManager.Type.ReplayEnded, null);
            return;
        }

        setCurrentTime(replay.getStartingTime());
        replayPlaying = true;
        switch (replay.getType()) {
            case Replay.TYPE_PLAYER_TURN:
                setCurrentPlayerTurn(replay.getPlayerNumber(), replay.getWormNumber());
                break;
            case Replay.TYPE_TURRETS:
                shootTurrets();
                break;
        }
        getWindHandler().setWind(replay.getWind());
    }
}
