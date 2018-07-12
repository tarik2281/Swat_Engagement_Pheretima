package de.paluno.game.interfaces;

public class UserName {

    private String userName;
    private String[] wormNames;

    public UserName() {
    }

    public UserName(String userName, String[] wormNames) {
        this.userName = userName;
        this.wormNames = wormNames;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setWormNames(String[] wormNames) {
        this.wormNames = wormNames;
    }

    public String getUserName() {
        return userName;
    }

    public String[] getWormNames() {
        return wormNames;
    }

    @Override
    public String toString() {
        return userName;
    }
}
