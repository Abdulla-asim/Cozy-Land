package com.mygdx.game.Resources;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Wood implements Resource{
    public static int WOOD = 200;

    protected float stateTime = 0f;
    public TextureRegion currentFrame;

    // Wood Range bounds
    public Rectangle rangeBounds;

    // Resources animations
    TextureAtlas atlas;
    public Animation<TextureRegion> animation;

    // Coords
    public float x;
    public float y;

    public Wood () {
        super();
        atlas = new TextureAtlas("atlas/Resources/Resources.atlas");
        animation = new Animation<>(franeDuration, atlas.findRegions("Resources_W_Spawn"));
    }

    public void update(float deltaTime, float x, float y) {
        stateTime += deltaTime;
        currentFrame = animation.getKeyFrame(stateTime);

        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch spriteBatch) {
        // Draw current frame
        spriteBatch.draw(currentFrame, x, y);
        // Range bounds
        rangeBounds = new Rectangle(x + 32, y + 25, width/2, width/4);
    }
}
