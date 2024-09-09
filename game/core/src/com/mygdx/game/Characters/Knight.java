package com.mygdx.game.Characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class Knight extends Character{

    // Extra Animations
    Animation<TextureRegion> idleAnimation;
    Animation<TextureRegion> runAnimation;
    Animation<TextureRegion> sideAttack;
    Animation<TextureRegion> upAttack;

    // Knight Offsets
    public float knightOffsetX = 5;
    public float knightOffsetY = 15;

    private Knight(float x1, float y1, int speed, float attackPower, float  hp) {
        super(type.KNIGHT, x1, y1, speed, attackPower, hp);
    }

    public Knight(TextureAtlas atlas, float x1, float y1) {
        this(x1, y1, 70, 12, 70);

        // EXTRA ANIMATIONS
        idleAnimation = new Animation<>(frameDuration, atlas.findRegions("Warrior_Purple_Idle"), Animation.PlayMode.LOOP);
        runAnimation = new Animation<>(frameDuration, atlas.findRegions("Warrior_Purple_Run"), Animation.PlayMode.LOOP);
        sideAttack = new Animation<>(frameDuration, atlas.findRegions("Warrior_Purple_Attack_1"), Animation.PlayMode.LOOP);
        upAttack = new Animation<>(frameDuration, atlas.findRegions("Warrior_Purple_Attack_1"), Animation.PlayMode.LOOP);


        // Collision bounds
        bounds = new Rectangle(x1 + offsetX, y1 + offsetY, idleAnimation.getKeyFrame(0).getRegionWidth() - 2*offsetX, idleAnimation.getKeyFrame(0).getRegionHeight() - 2*offsetX);
        // Range bounds
        rangeBounds = new Rectangle(bounds.x - knightOffsetX, bounds.y - knightOffsetY, bounds.width + 10, bounds.height + 25 );
        // Sight
        sight = new Circle(x1 + offsetX + knightOffsetX, y1 + offsetY + knightOffsetY, 100);

    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        switch (currentState) {
            case RUN:
            case RUN_FLIP:
                target = null;
                performMovement(deltaTime, knightOffsetX, knightOffsetY);
        }
    }

    @Override
    public void render(SpriteBatch batch, float deltaTime) {
        if(!isDead()) update(deltaTime);
        if (currentState != state.DEAD && targetDestroyed) currentState = state.IDLE;

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
//                currentFrame = deadFrame;
                break;
        }
        if (isSelected) batch.draw(selectionIndicator, x1 + offsetX - 17, y1 + offsetY - 30);
        if (currentFrame != null) {
            if (currentState != state.DEAD) batch.draw(currentFrame, x1, y1);
            else batch.draw(deadFrame, x1 + 60, y1 + 60); // iF dead
        }
    }



}
