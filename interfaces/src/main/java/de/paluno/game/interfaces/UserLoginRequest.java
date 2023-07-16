package de.paluno.game.interfaces;

import de.karaca.net.core.NetPayload;

@NetPayload
public class UserLoginRequest {

    @NetPayload
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
    private boolean udpEnabled;

    public UserLoginRequest() {

    }

    public UserLoginRequest(String userName, String[] wormNames, boolean udpEnabled) {
        this.name = userName;
        this.wormNames = wormNames;
        this.udpEnabled = udpEnabled;
    }

    public String getName() {
        return name;
    }

    public String[] getWormNames() {
        return wormNames;
    }

    public boolean isUdpEnabled() {
        return udpEnabled;
    }
}
