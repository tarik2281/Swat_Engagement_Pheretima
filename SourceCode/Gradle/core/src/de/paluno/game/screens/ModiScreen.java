package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.paluno.game.Assets;
import de.paluno.game.Config;
import de.paluno.game.NetworkClient;
import de.paluno.game.SEPGame;

public class ModiScreen extends ScreenAdapter implements Loadable {
    private Stage stage;
    private Table tableBackground, tableTextButton;
    private Skin skin;
    private SEPGame game;
    private ElementGUI elementGUI;
    private Image imageBackground;
    private TextButton textButtonPlay, textButtonOnline, textButtonLocal;
    private TextButton selectedModiButton;
    private NetworkClient client;
    private int modi = 1;
    private TextButton textButtonClose;
    private String errorText;

    public ModiScreen(SEPGame game) {
        super();
        this.game = game;
        elementGUI = new ElementGUI();
    }

    public ModiScreen(SEPGame game, String errorText) {
        super();
        this.game = game;
        elementGUI = new ElementGUI();
        this.errorText = errorText;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        tableBackground = new Table();
        tableTextButton = new Table();
        skin = elementGUI.getSkin();

        tableBackground.setFillParent(true);
        imageBackground = elementGUI.createBackground(game.getAssetManager().get(Assets.menuBackground));
        tableBackground.setBackground(imageBackground.getDrawable());

        textButtonLocal = elementGUI.createTextButton("Local");
        textButtonLocal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedTextButton(textButtonLocal);
                modi = 1;
            }
        });
        textButtonLocal.setSize(300, 100);
        textButtonOnline = elementGUI.createTextButton("Online");
        textButtonOnline.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                elementGUI.setSelectedTextButton(textButtonOnline);
                modi = 2;

            }
        });

        textButtonClose = elementGUI.createTextButton("Beenden");
        textButtonClose.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.exit(0);
            }
        });

        textButtonOnline.setSize(300, 100);
        textButtonPlay = elementGUI.createTextButton("Start");
        textButtonPlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (modi == 1){
                    game.setLocalScreen();
                }else if (modi == 2) {
                    Dialog connectionDialog = showConnectionDialog();
                    client = new NetworkClient("localhost");
                    client.setConnectionListener(new NetworkClient.ConnectionListener() {
                        @Override
                        public void onConnectionResult(NetworkClient client, int result) {
                            switch (result) {
                                case NetworkClient.RESULT_CONNECTION_SUCCESS:
                                    game.setLoginScreen(client);
                                    break;
                                case NetworkClient.RESULT_CONNECTION_FAILED:
                                    connectionDialog.hide();
                                    showErrorDialog("Verbindung zum Server konnte\nnicht aufgebaut werden.");
                                    break;
                            }
                        }
                    });
                    client.setDisconnectionListener(client -> {
                        game.setNextScreen(new ModiScreen(game, "Verbindung zum Server abgebrochen"));
                    });
                    client.connect();
                }
            }
        });

        textButtonLocal.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        textButtonOnline.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        tableTextButton.setFillParent(true);
        tableTextButton.add(textButtonLocal).size(350,80).pad(20);
        tableTextButton.add(textButtonOnline).size(350,80).pad(20).row();
        tableTextButton.add(textButtonPlay).size(200,60).padLeft(30);
        tableTextButton.add(textButtonClose).size(200,60).padRight(30);

//        stage.setDebugAll(true);

        elementGUI.setSelectedTextButton(textButtonLocal);

        stage.addActor(tableBackground);
        stage.addActor(tableTextButton);

        if (errorText != null)
            showErrorDialog(errorText);

        Gdx.input.setInputProcessor(stage);
    }

    private Dialog showConnectionDialog() {
        Dialog dialog = new Dialog("Online", skin);
        dialog.text("Verbindung zum Server wird hergestellt...");
        dialog.getContentTable().pad(10);
        dialog.show(stage);
        return dialog;
    }

    private void showErrorDialog(String text) {
        Dialog dialog = new Dialog("Fehler", skin);
        dialog.text(text);
        dialog.getContentTable().pad(10);
        dialog.button("OK");
        dialog.getButtonTable().pad(10);
        dialog.show(stage);
    }

    public void setSelectedModiButton(TextButton button) {
        //GREY Transparent 1,1,1,0.4
        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedModiButton = button;
        // WHITE Transparent 1,1,1,1
        if (selectedModiButton != null)
            selectedModiButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.LobbyScreenAssets);
        return false;
    }
}
