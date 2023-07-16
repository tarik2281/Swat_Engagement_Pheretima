package de.paluno.game.interfaces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyData {

    public int id;
    public String name;
    public int mapNumber;
    public int numWorms;
    public int creatingUserId;

    @Override
    public String toString() {
        return name;
    }
}
