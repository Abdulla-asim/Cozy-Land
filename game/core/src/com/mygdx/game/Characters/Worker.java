package com.mygdx.game.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Objects.MyTree;

public class Worker extends Character{

    // Animation data
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> runAnimation;
    Animation<TextureRegion> chopAnimation;

    // Chop Task
    private Timer.Task chopTask;
    public boolean isChopping = false;

    // Worker OFFSETS
    public float workerOffsetX = 5;
    public float workerOffsetY = 10;

    // CONSTRUCTORS
    public Worker(TextureAtlas atlas, float x1, float y1) {
        super(type.KNIGHT, x1, y1, 20, 5, 50);

        // More Animations
        // atlas = new TextureAtlas("atlas/Troops/Pawn_Purple.atlas");
        // WORKER ANIMATIONS
        runAnimation = new Animation<>(frameDuration, atlas.findRegions("Worker_Purple_Run"), Animation.PlayMode.LOOP);
        idleAnimation = new Animation<>(frameDuration, atlas.findRegions("Worker_Purple_Idle"), Animation.PlayMode.LOOP);
        chopAnimation = new Animation<>(frameDuration, atlas.findRegions("Worker_Purple_Chop"), Animation.PlayMode.LOOP);

        // Collision Bounds
        bounds = new Rectangle(x1 + offsetX, y1 + offsetY, idleAnimation.getKeyFrame(0).getRegionWidth() - 2*offsetX, idleAnimation.getKeyFrame(0).getRegionHeight() - 2*offsetX);
        // Range Bounds
        rangeBounds = new Rectangle(bounds.x - workerOffsetX, bounds.y - workerOffsetY, bounds.width + 10, bounds.height + 20 );
        // sight
        sight = new Circle(x1 + offsetX + workerOffsetX, y1 + offsetY + workerOffsetY, 50);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        switch (currentState) {
            case RUN:
            case RUN_FLIP:
                if (isChopping) stopChopping(prevState);
                target = null;
                performMovement(deltaTime, workerOffsetX, workerOffsetY);
        }
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        // Update the Worker
        if (!isDead()) update(deltaTime);

        if (targetDestroyed) currentState = state.IDLE;

        // Current frame to be rendered
        TextureRegion currentFrame = null;

        switch (currentState) {
            case RUN:
                currentFrame = runAnimation.getKeyFrame(stateTime);
                if (currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                prevState = state.RUN;
                break;

            case RUN_FLIP:
                currentFrame = runAnimation.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                prevState = state.RUN_FLIP;
                break;

            case CHOP:
                currentFrame = chopAnimation.getKeyFrame(stateTime);
                if (currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                break;

            case CHOP_FLIP:
                currentFrame = chopAnimation.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                break;

            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX() && prevState == state.RUN_FLIP || currentFrame.isFlipX() && prevState == state.RUN)
                    currentFrame.flip(true, false);
                break;
            case DEAD:
                currentFrame = deadFrame;
        }
        if (isSelected) batch.draw(selectionIndicator, x1 + offsetX - 17, y1 + offsetY - 30);
        if (currentFrame != null) {
            if (currentState != state.DEAD) batch.draw(currentFrame, x1, y1);
            else batch.draw(deadFrame, x1 + 60, y1 + 60); // iF dead
        }
    }

    public void chop(MyTree tree) {
        if (isChopping || tree.isChopped()) return;

        targetDestroyed = false;
        isChopping = true;
        target = tree;
        currentState = target.getBounds().x < bounds.x ? state.CHOP_FLIP : state.CHOP ;
        stateTime = 0f;

        // Sync the tree and worker animations
        chopTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (tree.getHp() > 0) {
                    tree.hit(Worker.this); // Trigger the tree's hit animation
                } else {
                    stopChopping(state.IDLE);
                }
            }
        }, 0.35f, 0.8f);
    }

    private void stopChopping(state nextState) {
        if (chopTask != null) {
            chopTask.cancel(); // Stop the task
        }
        isChopping = false;
        currentState = nextState; // Change the worker's state to idle or any other appropriate state
    }

    public boolean isMoving() {
        return currentState == state.RUN || currentState == state.RUN_FLIP;
    }

    public boolean isAt(float x, float y) {
        return this.x1 == x && this.y1 ==y1;
    }
}
