package de.paluno.game.interfaces;

public class UserLoginRequest {

    public static class Result {
        private boolean success;

        public Result() {

        }

        public Result(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private String name;
    private String[] wormNames;

    public UserLoginRequest() {

    }

    public UserLoginRequest(String userName, String[] wormNames) {
        this.name = userName;
        this.wormNames = wormNames;
    }

    public String getName() {
        return name;
    }

    public String[] getWormNames() {
        return wormNames;
    }
}
