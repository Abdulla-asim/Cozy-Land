package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface Renderable {

    public float getY();
    public Rectangle getBounds();
    public void render(SpriteBatch spriteBatch, float deltaTime);
}
