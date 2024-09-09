package com.mygdx.game.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.game.CozyLand;
import com.mygdx.game.tween.ActorAccessor;


public class MainMenu implements Screen {

    // Attributes
    private Stage stage;
    private Skin skin;
    private Table table;
    Label heading;
    TextButton playButton;
    TextButton exitButton;


    // Viewport and camera
    private FillViewport viewport;
    private OrthographicCamera camera;

    // Animations
    TweenManager tweenManager;
    
    @Override
    public void show() {
        // setting FitViewport
        camera = new OrthographicCamera();
        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.update();

        // The stage where everything happens
        stage = new Stage();

        // Allow the stage to handle inputs
        Gdx.input.setInputProcessor(stage);

        // Texture atlas pack and skin
        skin = new Skin(Gdx.files.internal("UI/menuSkin.json"), new TextureAtlas("UI/Button.atlas"));

        // Table to manage all the actors(buttons) easily
        table = new Table();
        table.setBounds(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // set position to the origin and span the whole screen

        // Creating a heading
        heading = new Label(CozyLand.TITLE, skin);
        heading.setFontScale(1.5f);

        // Play button
        playButton = new TextButton("PLAY", skin);
        playButton.pad(10,20,25,20);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GamePlay());
            }
        });

        // Exit button
        exitButton = new TextButton("EXIT", skin); // EXIT button with the textButtonStyle
        exitButton.pad(10,20,25,20); // Font Padding (in pixels)
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Adding elements to the table (putting stuff together)
        table.add(heading).width(360).height(100).pad(10,20,10,20); // Add the heading
        table.getCell(heading).spaceBottom(60);
        table.row(); // Adding a row (next line)
        table.add(playButton);
        table.row();
        table.add(exitButton); // Add button to the table
        stage.addActor(table); // Add table to the stage

        // Animations in the MAIN MENU (fade in)
        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class, new ActorAccessor());

//        Timeline.createSequence().beginSequence()
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0,1,0))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0,0.5f,0))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(1,1,0))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0,1,0.2f))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0.5f,1,0))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0,1,0.5f))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0,0.5f,0.5f))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(0.2f,1,0))
//                .push(Tween.to(heading, ActorAccessor.RGB, 1f).target(1,1,1))
//                .end().repeat(Tween.INFINITY, 0).start(tweenManager);

        // Buttons fade in
        Timeline.createSequence().beginSequence()
                .push(Tween.set(playButton, ActorAccessor.ALPHA).target(0)) // Set the alpha value of play button to 0
                .push(Tween.set(exitButton,ActorAccessor.ALPHA).target(0)) // Set the alpha value of exit button to 0
                .push(Tween.from(heading, ActorAccessor.ALPHA, .5f).target(0))
                .push(Tween.to(playButton, ActorAccessor.ALPHA, .3f).target(1))
                .push(Tween.to(exitButton, ActorAccessor.ALPHA, .3f).target(1))
                .end().start(tweenManager); // start tweenManager to start the timeline animation

        // Table fade in
        Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
        Tween.from(table, ActorAccessor.Y, .5f).target(Gdx.graphics.getHeight() / 5f).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f,0.2f,0.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        tweenManager.update(delta);

        stage.draw();
    }

    @Override
    public void resize(int x, int y) {
        // Updating the fit viewport
        camera.update();
        viewport.update(x,y,true);
        table.invalidateHierarchy();
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
        playButton.clear();
        playButton.remove();
        exitButton.clear();
        exitButton.remove();
        heading.clear();
        heading.remove();
        skin.dispose();
        stage.dispose();
        table.clear();
    }
}
