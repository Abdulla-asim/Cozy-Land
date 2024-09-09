package com.mygdx.game.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.tween.SpriteAccessor;

public class Splash implements Screen {

    private Sprite splash;
    private SpriteBatch batch;
    private TweenManager tweenManager;

    private FitViewport viewport;

    @Override
    public void show() {
        // Apply vsync preferences
        Gdx.graphics.setVSync(true);

        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        batch = new SpriteBatch();
        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor()); // Tells the manager about our Sprite class's SpriteAccessor

        splash = new Sprite(new Texture("img/splash.png")); // Takes a texture and creates a sprite for the splash screen
        splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Set the initial ALPHA value to 0.
        splash.setAlpha(0);
        // Actual animation is done by "to", increase or decrease the value from 0 "to" in 2 seconds.
        // Fading out is done by "repeatYoyo"
        Tween.to(splash, SpriteAccessor.ALPHA, 1).target(1).repeatYoyo(1, 1f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu()); // cahnged this
            }
        }).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        // Clear the buffer and screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tweenManager.update(delta); // delta is the time in seconds from the last frame to the current frame (for fps consistency)

        batch.begin();
        splash.draw(batch);
        batch.end();

    }

    @Override
    public void resize(int x, int y) {
        viewport.update(x, y, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        splash.getTexture().dispose();
    }
}
