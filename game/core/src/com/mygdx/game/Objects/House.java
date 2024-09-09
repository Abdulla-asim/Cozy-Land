package com.mygdx.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class House extends Object{

    // File Path constructor
    public House(String internalPath, float x, float y) {
        this(new Texture(internalPath), x, y);
    }

    // Texture constructor
    public House(Texture texture, float x, float y){
        this(new TextureRegion(texture), x, y);
    }

    // Texture Region constructor
    public House(TextureRegion textureRegion, Float x, Float y) {
        super(textureRegion, x, y, 200);
    }

    // Render Method
    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        batch.draw(textureRegion, x, y);
    }
}
