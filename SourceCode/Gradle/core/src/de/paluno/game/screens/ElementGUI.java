package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import javax.xml.soap.Text;

public class ElementGUI {

    private Texture texture;
    private Image image;

    private TextureRegion textureRegion;
    private TextureRegionDrawable textureRegionDrawable;
    private ImageButton imageButton;

    private TextField.TextFieldStyle textFieldStyle;
    private TextField textField;
    private Skin skin;


    public ImageButton createButton(Texture texture) {
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        imageButton = new ImageButton(textureRegionDrawable);
        return imageButton;
    }

    public TextField createTextField() {
        Text text;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
//        textField = new TextField("", skin);
        textFieldStyle = skin.get(TextField.TextFieldStyle.class);
        textField = new TextField("", textFieldStyle);
        textField.setSize(150, 50);
//        textFieldStyle.font.getData().setScale(1);

        return textField;
    }

    public Image createBackground(Texture texture) {
        image = new Image((new TextureRegionDrawable(new TextureRegion(texture))));
        return image;
    }
}
