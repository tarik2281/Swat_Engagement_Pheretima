package de.paluno.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ElementGUI {

    private Image image;

    private TextureRegion textureRegion;
    private TextureRegionDrawable textureRegionDrawable;

    private TextField.TextFieldStyle textFieldStyle;
    private TextField textField;

    private ImageButton imageButton;
    private ImageButton selectedImageButton, selectedImageButton2;

    private TextButton textButton;
    private TextButton selectedTextButton;

    private Skin skin = new Skin(Gdx.files.internal("sgx-ui/sgx-ui.json"));


    public Skin getSkin() {
        return skin;
    }



    public ImageButton createButton(Texture texture) {
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        imageButton = new ImageButton(textureRegionDrawable);
        imageButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);
        imageButton.pad(10);
        return imageButton;
    }

    public ImageButton createWeaponButton(Texture texture) {
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        imageButton = new ImageButton(textureRegionDrawable);
        return imageButton;
    }


    public TextButton createTextButton(String name) {
        textButton = new TextButton(name, skin);
        textButton.setSize(200, 60);
        textButton.pad(10);
        return textButton;
    }


    public TextField createTextField(String name) {

        textFieldStyle = skin.get(TextField.TextFieldStyle.class);
        textField = new TextField(name, textFieldStyle);
        textField.setSize(200, 50);

        return textField;
    }

    public Image createBackground(Texture texture) {
        image = new Image((new TextureRegionDrawable(new TextureRegion(texture))));
        return image;
    }

    public void setSelectedTextButton(TextButton button) {
        if (selectedTextButton != null)
            selectedTextButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedTextButton = button;

        if (selectedTextButton != null)
            selectedTextButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setSelectedImageButton(ImageButton button) {
        if (selectedImageButton != null)
            selectedImageButton.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedImageButton = button;

        if (selectedImageButton != null)
            selectedImageButton.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void setSelectedImageButton2(ImageButton button) {
        if (selectedImageButton2 != null)
            selectedImageButton2.setColor(1.0f, 1.0f, 1.0f, 0.4f);

        selectedImageButton2 = button;

        if (selectedImageButton2 != null)
            selectedImageButton2.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

}
