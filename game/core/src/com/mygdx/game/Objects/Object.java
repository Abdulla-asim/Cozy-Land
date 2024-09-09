package com.mygdx.game.Objects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Renderable;

public abstract class Object implements Renderable {

    // States
    protected enum state{IDLE, HIT, HIT_FLIP, CHOPPED, COLLECTED};
    protected state currentState;

    // Attributes
    public float x, y;
    public float hp;
    protected TextureRegion textureRegion;
    public Rectangle bounds; // Bounds for the Object
    public boolean hasBounds = true;
    public final float offsetX = 64;
    public final float offsetY = 20;

    // Frame Duration
    public static float frameDuration = 0.2f;

    // Selection Pointer
//    protected Texture selectionTexture;
//    public boolean isSelected = false;

    // Range Bounds
    public Rectangle rangeBounds;


    public Object (TextureRegion textureRegion, float x, float y, float hp, Rectangle bounds) {
        this.textureRegion = textureRegion;
        this.currentState = state.IDLE;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.bounds = bounds;
    }

    // Texture Region and HP method
    public Object(TextureRegion textureRegion, float x, float y, float hp) {
        this.textureRegion = textureRegion;
        this.currentState = state.IDLE;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.bounds = new Rectangle(x + offsetX, y + offsetY, textureRegion.getRegionWidth() - 2*offsetX, textureRegion.getRegionHeight() - 2*offsetX);
    }

    // Check if point is inside the object
    public boolean contains(float x, float y) {
        return rangeBounds.contains(x, y);
    }

    // Get bounding rectangle
    public Rectangle getBounds() {
        return bounds;
    }
    public boolean hasBounds() { return hasBounds; }
    public float getHp() { return hp; }
    public float getY() { return y-50; }
}
