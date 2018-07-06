package de.paluno.game.server;

import java.util.ArrayList;
import java.util.Random;

public class Match {

    private int currentTick;

    private int currentPlayerIndex;
    private ArrayList<Player> players;
    private int numPlayersAlive;

    private Random windRandomizer;

    public Match() {
        currentTick = 0;
        currentPlayerIndex = -1;
        numPlayersAlive = 0;

        players = new ArrayList<>();
        windRandomizer = new Random();
    }

    public Player addPlayer(User user, int number) {
        Player player = new Player(user, number);
        player.setDefeatedListener(() -> numPlayersAlive--);
        players.add(player);
        numPlayersAlive++;
        return player;
    }
}
