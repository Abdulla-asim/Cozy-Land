package com.mygdx.game.Objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Characters.Worker;
import com.mygdx.game.Resources.Wood;

public class MyTree extends Object {

    // Atlas and animations
    TextureAtlas treeAtlas;
    private Animation<TextureRegion> idleAnimation, hitAnimation; // animation
    TextureRegion currentFrame = null;

    // Attributes
    private float stateTime; // elapsed time ig

    // Tree Wood
    public Wood wood;
    public boolean dropWood = false;

    // NON-hp constructor
    public MyTree(TextureRegion textureRegion, Float x, Float y) {
        this(textureRegion, x, y, 50);
    }

    // Texture Region and HP constructor
    public MyTree (TextureRegion textureRegion, Float x, Float y, float hp) {
        super(textureRegion, x, y, hp,  new Rectangle(x + (1/2.6f)*textureRegion.getRegionWidth(), y + 20, textureRegion.getRegionWidth() - (3/4f)*textureRegion.getRegionWidth(), textureRegion.getRegionHeight() - (3/4f)* textureRegion.getRegionHeight()));
        this.stateTime = 0f;

        // Wood
        wood = new Wood();

        // More animations
        treeAtlas = new TextureAtlas("atlas/Tree.atlas");
        hitAnimation = new Animation<>(frameDuration, treeAtlas.findRegions("Tree_Hit"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<>(frameDuration, treeAtlas.findRegions("Tree_Idle"), Animation.PlayMode.LOOP);

        // Range Bounds
        rangeBounds = new Rectangle(bounds.x - 5, bounds.y , bounds.width + 10, bounds.height + 40);
    }

    // Chop a tree
    public void hit(Worker worker) {
        currentState = (worker.getX() < x ) ? state.HIT_FLIP : state.HIT;
        float damage = worker.getAttackPower();
        if (hp > 0) {
            hp = Math.max(0, hp - damage);
            if (hp <= 0) {
                currentState = state.CHOPPED;
            }
        }

        // Stop the HIT animation after a short delay
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (currentState != state.CHOPPED) {
                    currentState = state.IDLE;
                }
            }
        }, 0.25f); // Adjust the delay to match the hit animation duration
    }

    // Render function
    public void render (SpriteBatch batch, float deltaTime) {
        // Update time
        stateTime += deltaTime;

        // Switch the frame(Texture) to be rendered
        switch (currentState) {
            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(stateTime);
                break;
            case HIT:
                currentFrame = hitAnimation.getKeyFrame(stateTime);
                System.out.println("REMAINING HP: " + hp);
                break;
            case HIT_FLIP:
                currentFrame = hitAnimation.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                System.out.println("REMAINING HP: " + hp);
                break;
            case CHOPPED:
                currentFrame = treeAtlas.findRegion("Tree_Chopped");
                hasBounds = false;
                dropWood = true;
                break;
            case COLLECTED:
                dropWood = false;
                wood = null;
                break;
        }

        // Render next frame
        if (dropWood && wood != null) {
            wood.update(deltaTime, x - 50, y - 50);
            wood.render(batch);
        }
        batch.draw(currentFrame, x , y);
    }

    public boolean isChopped() {
        return currentState == state.CHOPPED;
    }

    public void collectWood() {
        currentState = state.COLLECTED;

        // INCREMENT THE WOOD RESOURCE
        Wood.WOOD += 100;
    }
}
