package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.paluno.game.Assets;
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

    public ModiScreen(SEPGame game) {
        super();
        this.game = game;
        elementGUI = new ElementGUI();
    }

    @Override
    public void show() {
        stage = new Stage();
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
                    client = new NetworkClient("178.202.240.241");
                    client.setConnectionListener(new NetworkClient.ConnectionListener() {
                        @Override
                        public void onConnectionResult(NetworkClient client, int result) {
                            switch (result) {
                                case NetworkClient.RESULT_CONNECTION_SUCCESS:
                                    game.setLoginScreen(client);
                                    break;
                                case NetworkClient.RESULT_CONNECTION_FAILED:
                                    new Dialog("Server Connection", elementGUI.getSkin()) {
                                        protected void result (Object object) {
                                            System.out.println("Chosen: " + object);
                                        }
                                    }.text("Connection failed").button("Close", true).show(stage);
                                    break;
                            }
                        }
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

        Gdx.input.setInputProcessor(stage);
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
    public boolean load(AssetManager manager) {
        Assets.loadAssets(manager, Assets.LobbyScreenAssets);
        return false;
    }
}
