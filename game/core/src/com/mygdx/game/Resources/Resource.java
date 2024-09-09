package com.mygdx.game.Resources;

public interface Resource {
    // Frame duration
    float franeDuration = 0.1f;
    float width = 128;

    void update(float deltaTime, float x, float y);
}
