package com.mygdx.game.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;

public class Goblin extends Character{

    // ANIMATION MATERIAL
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> runAnimation;
    Animation<TextureRegion> sideAttack;
    Animation<TextureRegion> upAttack;


    // GOBLIN TASK
    private Timer.Task fightTask;
    private boolean isFighting = false;

    // OFFSETS
    public float goblinOffsetX = 10;
    public float goblinOffsetY = 16;


    public Goblin(TextureAtlas atlas, float x1, float y1) {
        this(x1, y1, 70, 6, 40);

        // EXTRA ANIMATIONS
        idleAnimation = new Animation<>(frameDuration, atlas.findRegions("Torch_Blue_Idle"), Animation.PlayMode.LOOP);
        runAnimation = new Animation<>(frameDuration, atlas.findRegions("Torch_Blue_Run"), Animation.PlayMode.LOOP);
        sideAttack = new Animation<>(frameDuration, atlas.findRegions("Torch_Blue_Attack_Right"), Animation.PlayMode.LOOP);
        upAttack = new Animation<>(frameDuration, atlas.findRegions("Torch_Blue_Attack_Up"), Animation.PlayMode.LOOP);


        // Collision bounds
        bounds = new Rectangle(x1 + offsetX, y1 + offsetY, idleAnimation.getKeyFrame(0).getRegionWidth() - 2*offsetX, idleAnimation.getKeyFrame(0).getRegionHeight() - 2*offsetX);
        // Range bounds
        rangeBounds = new Rectangle(bounds.x - goblinOffsetX, bounds.y - goblinOffsetY, bounds.width + 20, bounds.height + 32 );
        // Sight Bounds
        sight = new Circle(x1 + offsetX + goblinOffsetX, y1 + offsetY + goblinOffsetY, 150);
    }

    public Goblin(float x1, float y1, int speed, float attackPower, float hp) {
        super(type.GOBLIN, x1, y1, speed, attackPower, hp);
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        switch (currentState) {
            case RUN:
            case RUN_FLIP:
                target = null;
                performMovement(deltaTime, goblinOffsetX, goblinOffsetY);
        }
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        if(!isDead()) update(deltaTime);
        if (currentState != state.DEAD &&targetDestroyed) currentState = state.IDLE;

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

            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX() && prevState == state.RUN_FLIP || currentFrame.isFlipX() && prevState == state.RUN)
                    currentFrame.flip(true, false);
                break;

//            case FIGHT:
//                currentFrame = sideAttack.getKeyFrame(stateTime);
//                if (!currentFrame.isFlipX() && prevState == state.RUN_FLIP || currentFrame.isFlipX() && prevState == state.RUN)
//                    currentFrame.flip(true, false);
//                break;
            case FIGHT:
                currentFrame = sideAttack.getKeyFrame(stateTime);
                if (currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                break;

            case FIGHT_FLIP:
                currentFrame = sideAttack.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                break;

            case FIGHT_UP:
                currentFrame = upAttack.getKeyFrame(stateTime);
                if (!currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                break;

            case DYING:
                // THIS STATE IS NOT BEING USED RN
                if (stateTime >= deathAnimation.getAnimationDuration())
                    currentState = state.DEAD;
                currentFrame = deathAnimation.getKeyFrame(stateTime);
                break;
            case DEAD:
                currentFrame = deadFrame;
                break;
        }
        if (isSelected && currentState != state.DEAD) batch.draw(selectionIndicator, x1 + offsetX - 17, y1 + offsetY - 30);
//        if (currentState == state.DYING) batch.draw(currentFrame, x1 + goblinOffsetX , y1 + goblinOffsetY ); else
        if (currentFrame != null) {
            if (currentState != state.DEAD) batch.draw(currentFrame, x1, y1);
            else batch.draw(deadFrame, x1 + 60, y1 + 60); // iF dead
        }
    }

    @Override
    protected void performMovement(float deltaTime, float characterSpecificOffsetX, float characterSpecificOffsetY) {
        super.performMovement(deltaTime, characterSpecificOffsetX, characterSpecificOffsetY);
    }
}
