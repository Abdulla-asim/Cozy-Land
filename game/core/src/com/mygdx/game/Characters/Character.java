package com.mygdx.game.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.Objects.Object;
import com.mygdx.game.Renderable;
import com.mygdx.game.screens.GamePlay;

public abstract class Character implements Renderable {

    // STATES
    protected enum state{IDLE, RUN, RUN_FLIP, CHOP, CHOP_FLIP, CARRY_IDLE, CARRY_RUN, FIGHT, FIGHT_FLIP, DEAD, DYING, FIGHT_UP};
    public enum type{KNIGHT, GOBLIN};
    public type charType;
    protected state currentState;
    protected state prevState;

    // Attributes
    protected float x1, y1, x2, y2;
    public Rectangle bounds;
    protected float stateTime;
    protected float speed;
    protected float attackPower;
    public float hp;
    protected boolean isFighting = false;
    protected boolean targetDestroyed = false;
    public boolean hasBounds = true;
    public Renderable target;
    public final float offsetX = 80;
    public final float offsetY = 80;
    public boolean cantMove = false;

    // Tasks
    private Timer.Task fightTask;

    // Frame rate
    public static float frameDuration = 0.25f;

    // Selection Pointer
    public Texture selectionIndicator = new Texture("Tiny Swords (Update 010)/UI/Pointers/02.png");
    protected boolean isSelected = false;

    public static Animation<TextureRegion> deathAnimation;
    public TextureRegion deadFrame = new TextureRegion(new Texture("atlas/Dead/15.png"));

    // Range Bounds
    public Rectangle rangeBounds;
    public Circle sight;

    public Character(type type, float x1, float y1, int speed, float attackPower, float hp) {
        this.charType = type;
        this.currentState = state.IDLE;
        this.speed = speed;
        this.attackPower = attackPower;
        this.hp = hp;
        this.x1 = x1;
        this.y1 = y1;
        target = null;
    }

    public boolean contains(float x, float y) {
        return rangeBounds.contains(x, y);
    }

    public boolean isAt(float x, float y) {
        return x == x1 && y == y1;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void moveTo(float x, float y) {
        // final coordinates
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) speed = 200; // SPEED UP (SPECIAL)
        else speed = 70;

        x2 = x-offsetX;
        y2 = y-offsetY;
        stateTime = 0f; // Reset state time when starting to move
        currentState = (x2 < x1)? state.RUN_FLIP : state.RUN;
    }

    public void moveTo(Character character) {
        if (character.currentState == state.DEAD || character.cantMove) return;
        // final coordinates
        target = character;
        x2 = character.x1 ;
        y2 = character.y1 ;
        stateTime = 0f; // Reset state time when starting to move
        currentState = (x2 < x1)? state.RUN_FLIP : state.RUN;

        if (rangeBounds.overlaps(character.rangeBounds)) {
            attack(character);
        }
    }

    protected void performMovement(float deltaTime, float characterSpecificOffsetX, float characterSpecificOffsetY) {
        // Calculate the distance
        float dx = x2 - x1;
        float dy = y2 - y1;
        float distance  = (float) Math.sqrt(dx * dx + dy * dy); // distance formula

        if (distance < speed * deltaTime) {
            x1 = x2;
            y1 = y2;
            currentState = state.IDLE;
            stateTime = 0f; // Reset state time when not moving
        }
        else {
            float directionX = dx / distance;
            float directionY = dy / distance;
            x1 += directionX * speed * deltaTime;
            y1 += directionY * speed * deltaTime;
        }
        bounds.setPosition(x1 + offsetX, y1 + offsetY);
        if (sight != null) sight.setPosition(x1 + offsetX + characterSpecificOffsetX, y1 + offsetY + characterSpecificOffsetY);
        rangeBounds.setPosition(x1 + offsetX - characterSpecificOffsetX, y1 + offsetY - characterSpecificOffsetY);
    }

    public void attack(Character character) {
        if (character.isDead()) return;

        targetDestroyed = false;
        isFighting = true;
        target = character;
        currentState = target.getBounds().x < bounds.x ? state.FIGHT_FLIP : state.FIGHT;
        if (isGoblin() &&  target.getBounds().y > bounds.y + bounds.getHeight()) currentState = state.FIGHT_UP;

        stateTime = 0f;

        fightTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (character.getHp() > 0) {
                    character.damageBy(attackPower); // Attack Goblin
                    System.out.println( "HP: " + character.hp);
                } else if (!character.isDead())
                    kill(character); // Kill Goblin
            }
        }, 1, 1);
    }

    public void stopMoving() {
        currentState = state.IDLE;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
        x1 = x - offsetX;
        y1 = y - offsetY;
    }

    public void setX(float x) {
        this.x1 = x-offsetX;
        bounds.setX(x);
    }

    public void setY(float y) {
        this.y1 = y-offsetY;
        bounds.setY(y);
    }

    public void setDeathAnimation(Animation<TextureRegion> death) {
        deathAnimation = death;
    }

    public void kill(Character target) {
        if (fightTask != null)
            fightTask.cancel();
        if (target.fightTask != null)
            target.fightTask.cancel();

        currentState = state.IDLE;
//        targetDestroyed = true;
        target.currentState = state.DEAD;
        target.cantMove = true;
        System.out.println("Character dies..");
        target.hasBounds = false;

        isFighting = false;
    }

    public void dispose(Character character) {
        GamePlay.characters.remove("TestGoblin");
        GamePlay.renderables.remove(character);
    }

    public void setSelected(Boolean bool) { isSelected = bool; }
    public void setTarget(Object target) { this.target = target; }
    public void damageBy(float damageAmount) { hp -= damageAmount; }

//    public void setName(String name) { this.name = name; }

    public float getX() { return x1; }
    public float getY() { return y1; }
    public float getHp() { return hp; }
    public float getAttackPower() { return attackPower; }
    public boolean isDead() { return currentState == state.DEAD; }
    public boolean isKnight() { return charType == type.KNIGHT; }
    public boolean isGoblin() { return charType == type.GOBLIN; }

    public abstract void update(float deltaTime);
}
