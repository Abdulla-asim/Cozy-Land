package com.mygdx.game.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorAccessor implements TweenAccessor<Actor> {
    public static final int Y = 0, RGB = 1, ALPHA = 2;

    @Override
    public int getValues(Actor actor, int i, float[] returnVals) {
        switch (i) {
            case Y:
                returnVals[0] = actor.getY();
                return 1;

            case RGB:
                returnVals[0] = actor.getColor().r;
                returnVals[1] = actor.getColor().g;
                returnVals[2] = actor.getColor().b;
                return 2;

            case ALPHA:
                returnVals[0] = actor.getColor().a;
                return 3;

            default:
                assert false;
                return 0;
        }
    }

    @Override
    public void setValues(Actor actor, int i, float[] newVals) {
        switch (i) {
            case Y:
                actor.setY(newVals[0]);
                break;

            case RGB:
                actor.setColor(newVals[0], newVals[1], newVals[2], actor.getColor().a);
                break;

            case ALPHA:
                actor.setColor(actor.getColor().r, actor.getColor().g, actor.getColor().b, newVals[0]);
                break;

            default:
                assert false;
        }
    }
}
