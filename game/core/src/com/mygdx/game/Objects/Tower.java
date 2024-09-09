package com.mygdx.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tower extends Object{
    // File Path constructor
    public Tower(String internalPath, float x, float y) {
        this(new Texture(internalPath), x, y);
    }

    // Texture constructor
    public Tower(Texture texture, float x, float y) {
        this(new TextureRegion(texture), x, y);
    }

    // Texture Region constructor
    public Tower(TextureRegion textureRegion, Float x, Float y) {
        super(textureRegion, x, y , 2000);
    }

    // Render method
    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        batch.draw(textureRegion, x, y);
    }
}
