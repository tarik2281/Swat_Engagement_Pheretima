package de.paluno.game.server.test;

import com.esotericsoftware.kryonet.Connection;
import de.paluno.game.interfaces.PlayerData;
import de.paluno.game.server.Lobby;
import org.junit.Assert;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LobbyTest {

    private Lobby lobby;

    @Before
    public void setup() {
        lobby = new Lobby();

        Connection connection1 = mock(Connection.class);
        Connection connection2 = mock(Connection.class);

        lobby.addPlayerConnection(connection1);
        lobby.addPlayerConnection(connection2);

        PlayerData data1 = mock(PlayerData.class);
        PlayerData data2 = mock(PlayerData.class);
    }

    @Test
    public void test_shiftTurn() {
        lobby.shiftTurn(true);
        assertEquals(0, lobby.getCurrentPlayer().getNumber());
        //System.out.println("Testing shiftTurn()");
    }
}
