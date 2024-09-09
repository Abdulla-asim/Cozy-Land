package com.mygdx.game.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteAccessor implements TweenAccessor<Sprite> {

    public static final int ALPHA = 0;

    @Override
    public int getValues(Sprite sprite, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
                returnValues[0] = sprite.getColor().a;
                return 1; // return the number of returnValues that u assigned

            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Sprite sprite, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
                sprite.setAlpha(newValues[0]);
                break;

            default:
                assert false;
                break;
        }
    }
}
