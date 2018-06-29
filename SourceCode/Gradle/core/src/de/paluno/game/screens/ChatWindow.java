package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import de.paluno.game.NetworkClient;

public class ChatWindow {

    private NetworkClient client;
    private Stage stage;
    private TextArea textArea;
    private Skin skin;

    public ChatWindow(NetworkClient client) {
        this.client = client;
    }

    public void initialize() {


        skin = new Skin(Gdx.files.internal("uiskin.json"));

        stage = new Stage();

        textArea = new TextArea("", skin);

        stage.addActor(textArea);
        textArea.setAlignment(Align.bottomLeft);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
