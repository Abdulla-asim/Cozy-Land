package com.mygdx.game.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Characters.Character;
import com.mygdx.game.Characters.Goblin;
import com.mygdx.game.Characters.Knight;
import com.mygdx.game.Characters.Worker;
import com.mygdx.game.Objects.Object;
import com.mygdx.game.Objects.*;
import com.mygdx.game.Renderable;
import com.mygdx.game.Resources.Wood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class GamePlay implements Screen {
    // Multiple input processing
    public InputMultiplexer inputMultiplexer;
    // Game Labels and Skins
    public Label woodlabel, foodLabel, goldLabel;
    public Skin skin;
    public Texture labelBgTexture;
    public Stage stage;
    Table table;

    // Camera stuff
    Viewport viewport;
    TiledMap map;
    OrthographicCamera camera;
    TiledMapRenderer mapRenderer;

    // People
    public SpriteBatch sb;
    TextureAtlas atlas;

    // Object layer
    MapLayer layer;

    // Texture pools
    public static ArrayList<Renderable> renderables;
    public static Array<Object> objects;
    public static HashMap<String, Character> characters;

    // Selected THINGS
    Character selectedCharacter;
    Object selectedObject;

    @Override
    public void show() {
        // UI Settings
        stage = new Stage(new ScreenViewport());
        setUi();

        // Screen width and height
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Camera settings
        camera = new OrthographicCamera(w, h);
        viewport = new FitViewport(w, h, camera);
        camera.position.set(getCellWorldX(48), getCellWorldY(38), 0);
        camera.update();
//        viewport.setScreenPosition(viewport.getScreenX(), viewport.getScreenY()); // Setting viewport size to add UI components
//        viewport.apply(); // Apply viewport.setScreenPosition();

        // Map setting
        map = new TmxMapLoader().load("NewMap/untitled.tmx");

        // Getting the object layer
        layer =  map.getLayers().get("Objects");
        // Sprite Batch creation
        sb = new SpriteBatch();

        // Renderer for tiled map
        mapRenderer = new OrthogonalTiledMapRenderer(map);



        objects = new Array<>(); // Array of all the map objects
        // Adding all the objects already on map to objects array
        for (MapObject object : layer.getObjects()) {
            String objName = object.getName();
            Float x = object.getProperties().get("x", Float.class);
            Float y = object.getProperties().get("y", Float.class);

            atlas = new TextureAtlas("atlas/Tree.atlas"); // Get the tree atlas

            if (objName != null) {
                if (objName.contains("Tree")) {
                    objects.add(new MyTree(atlas.findRegion("Tree_Idle", 0), x, y)); // add new tree to array
                } else if (objName.contains("House")) {
                    objects.add(new House("Tiny Swords (Update 010)/Factions/Knights/Buildings/House/House_Red.png", x, y));
                } else if (objName.contains("Castle")) {
                    objects.add(new Castle("Tiny Swords (Update 010)/Factions/Knights/Buildings/Castle/Castle_Red.png", x, y));
                } else if (objName.contains("Tower")) {
                    objects.add(new Tower("Tiny Swords (Update 010)/Factions/Knights/Buildings/Tower/Tower_Red.png",  x, y));
                }
            }
        }

        // WORKER ANIMATION
        characters = new HashMap<>(); // Array of all the characters

        atlas = new TextureAtlas("atlas/Dead/Dead.atlas");
        Character.deathAnimation = new Animation<>(Character.frameDuration, atlas.findRegions("Dead"));;
        // WORKERS
        atlas = new TextureAtlas("atlas/Troops/Pawn_Purple.atlas");
        characters.put(String.format("Worker" + characters.size()), new Worker(atlas, getCellWorldX(38), getCellWorldY(46)));
        characters.put(String.format("Worker" + characters.size()), new Worker(atlas, getCellWorldX(37), getCellWorldY(47)));
        characters.put(String.format("Worker" + characters.size()), new Worker(atlas, getCellWorldX(8), getCellWorldY(12)));
        // KNIGHTS
        atlas = new TextureAtlas("atlas/Troops/Warrior_Purple.atlas");
        characters.put(String.format("Knight" + characters.size()), new Knight(atlas, getCellWorldX(36), getCellWorldY(40)));
        characters.put(String.format("Knight" + characters.size()), new Knight(atlas, getCellWorldX(36), getCellWorldY(40)));
        characters.put(String.format("Knight" + characters.size()), new Knight(atlas, getCellWorldX(9), getCellWorldY(14)));
        // GOBLINS
        atlas = new TextureAtlas("atlas/Goblins/Torch_Blue.atlas");
        characters.put(String.format("TestGoblin"), new Goblin(atlas, getCellWorldX(9), getCellWorldY(8)));

        // Initializing the renderables array
        renderables = new ArrayList<>();
        // Collect all renderable entities
        for (Object obj : objects) {
            renderables.add(obj);
        }
        renderables.addAll(characters.values());

        // HANDLING INPUTS
//        Gdx.input.setInputProcessor(new InputHandler());
        inputMultiplexer = new InputMultiplexer(new InputHandler(), stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Screen Movement
        handleCameraMovement();

        // Map Rendering
        camera.update(); // Must update the camera view here or the map will not move,
        mapRenderer.setView(camera); // sets the updated view of the camera to be rendered
        mapRenderer.render(); // Renders the updated view.

        // Makes the sprite not move with the camera
        sb.setProjectionMatrix(camera.combined);

        // Check for collisions before rendering
        for (Character character : characters.values()) {
            checkCollisions(character, delta); // Check collision of "character" with every object and other character
        }

        // BEGIN Sprite Rendering
        sb.begin();
        float deltaTime = Gdx.graphics.getDeltaTime(); // Get the delta time since the last frame

        // HIT BOX render
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
//
        // Sort renderables by their y-coordinate
        Collections.sort(renderables, new Comparator<Renderable>() {
            @Override
            public int compare(Renderable o1, Renderable o2) {
                return Float.compare(o2.getY(), o1.getY());
            }
        });

        // Render all entities
        for (Renderable renderable : renderables) {
            shapeRenderer.rect(renderable.getBounds().x, renderable.getBounds().y, renderable.getBounds().width, renderable.getBounds().height);
            if (renderable instanceof Character) {
                shapeRenderer.circle(((Character) renderable).sight.x, ((Character) renderable).sight.y, ((Character) renderable).sight.radius);
                shapeRenderer.rect(((Character) renderable).rangeBounds.x, ((Character) renderable).rangeBounds.y, ((Character) renderable).rangeBounds.width, ((Character) renderable).rangeBounds.height);
            } else if (renderable instanceof MyTree) {
                shapeRenderer.rect(((MyTree) renderable).rangeBounds.x, ((MyTree) renderable).rangeBounds.y, ((MyTree) renderable).rangeBounds.width, ((MyTree) renderable).rangeBounds.height);
                if (((MyTree) renderable).dropWood){
                    shapeRenderer.rect(((MyTree) renderable).wood.rangeBounds.x, ((MyTree) renderable).wood.rangeBounds.y, ((MyTree) renderable).wood.rangeBounds.width, ((MyTree) renderable).wood.rangeBounds.height);
                }
            }
            renderable.render(sb, deltaTime);
        }
//        shapeRenderer.end();
        sb.end();
        // END Sprite Rendering

        // UI COMPONENTS RENDERING
        updateLabelTexts();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int x, int y) {
        viewport.update(x,y, true);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        map.dispose();
        sb.dispose();
        atlas.dispose();
        stage.dispose();
        skin.dispose();
        labelBgTexture.dispose();
        inputMultiplexer.clear();
        woodlabel.clear();
        foodLabel.clear();
        goldLabel.clear();
        table.clear();
        renderables.clear();
        objects.clear();
        characters.clear();
    }

    public float getCellWorldX(int x) {
        return x * 64;
    }

    public float getCellWorldY(int y) {
        return y * 64;
    }

    public void checkCollisions(Character character, float deltaTime) {
        // Get Current Coordinates
        float oldX = character.getX() + character.offsetX;
        float oldY = character.getY() + character.offsetY;

        // Update position
        character.update(deltaTime);

        // Check overlapping with objects
        for (Object object : objects) {
            if (object.hasBounds() && character.getBounds().overlaps(object.getBounds()) ){
                // Stop the character from moving
                character.stopMoving();

                // Reset the Position of character (The bounding lines were intersecting before, So I modified the coordinates a little bit)
                if (character.getBounds().getX() < object.getBounds().getX()) oldX -= 2;
                else oldX += 2;

                if (character.getBounds().getY() < object.getBounds().getY()) oldY -= 2;
                else oldY += 2;

                character.setPosition(oldX, oldY);
            }
        }

        // Check overlapping with Characters
        for (Character othercharacter : characters.values()) {
            if (!character.cantMove && othercharacter.hasBounds && character != othercharacter ) {
                if (othercharacter.isGoblin() && character instanceof Knight && othercharacter.sight.overlaps(character.sight)) {
                    if (othercharacter.target != character) othercharacter.moveTo(character);
                    if (character.target != othercharacter) character.moveTo(othercharacter);
                }
                else if (othercharacter.isGoblin() && character instanceof Worker && othercharacter.sight.overlaps(character.sight))
                    if (!(othercharacter.target instanceof Knight) ) othercharacter.moveTo(character);
                if (character.getBounds().overlaps(othercharacter.getBounds())) {
                    // Stop the character
                    character.stopMoving();

                    // Reset the Position of character (The bounding lines were intersecting before, So I modified the coordinates a little bit)
                    if (character.getBounds().getX() < othercharacter.getBounds().getX()) oldX -= 2;
                    else oldX += 2;

                    if (character.getBounds().getY() < othercharacter.getBounds().getY()) oldY -= 2;
                    else oldY += 2;

                    character.setPosition(oldX, oldY);
                }
            }
        }
    }

    public boolean characterPresentAt (float x, float y) {
        for (Character character : characters.values()) {
            if (!character.isDead() && character.contains(x, y))
                return true;
        }
        return false;
    }

    // Move the screen using arrows
    private void handleCameraMovement() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                camera.translate(-32, 0);
            camera.translate(-10, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                camera.translate(32, 0);
            camera.translate(10, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                camera.translate(0, -32);
            camera.translate(0, -10);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                camera.translate(0, 32);
            camera.translate(0, 10);
        }
    }

    // Handle Inputs
    class InputHandler extends InputAdapter {

        // Special Keys
        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.ESCAPE)
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            return true;
        }

        // Mouse Inputs
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {

            // GET TOUCH COORDINATES
            Vector3 touchPosition = new Vector3(screenX, screenY, 0);
            // Convert to world coordinates
            camera.unproject(touchPosition);

            if (button == Input.Buttons.LEFT) {
                // FOR LEFT MOUSE CLICK
                if (characterPresentAt(touchPosition.x, touchPosition.y)) {
                    // Select a character
                    for (Character character : characters.values()) {
                        if (character.contains(touchPosition.x, touchPosition.y)) {
                            if (selectedCharacter != null) selectedCharacter.setSelected(false);
                            selectedCharacter = character;
                            character.setSelected(true);
                            break;
                        } else
                            System.out.println("Empty or Goblin");
                    }
                } else {
                    if (selectedCharacter != null) {
                        selectedCharacter.setSelected(false);
                        selectedCharacter = null;
                    }
                    for (Object object : objects) {
                        if (object instanceof MyTree && ((MyTree) object).dropWood) {
                            // IF the tree had dropped wood (doesn't matter if a character was elected or not)
                            if (((MyTree) object).wood.rangeBounds.contains(touchPosition.x, touchPosition.y)) {
                                // AND IF user clicked on that wood
                                ((MyTree) object).collectWood();
                            }
                        }
                    }
                }
            } else if (button == Input.Buttons.RIGHT) {
                // FOR RIGHT MOUSE CLICK
                // Chopping tree
                if (selectedCharacter != null && selectedCharacter.isKnight()) {
                    selectedCharacter.moveTo(touchPosition.x, touchPosition.y);
                    if (selectedCharacter instanceof Worker) {
                        for (Object object : objects) {
                            if (object instanceof MyTree && object.hasBounds && object.contains(touchPosition.x, touchPosition.y) && selectedCharacter.rangeBounds.overlaps(object.rangeBounds))
                                ((Worker) selectedCharacter).chop((MyTree) object);
                        }
                    } else if (selectedCharacter instanceof Knight) {
                        for (Character character : characters.values()) {
                            if ( character.hasBounds && character.contains(touchPosition.x, touchPosition.y) && selectedCharacter.rangeBounds.overlaps(character.rangeBounds))
                                (selectedCharacter).attack(character);
                        }
                    }
                }
            }
            return true;
        }
    }

    public void setUi() {
        table = new Table();
        labelBgTexture = new Texture("Tiny Swords (Update 010)/UI/Ribbons/Ribbon_Blue_3Slides.png");
        skin = new Skin(Gdx.files.internal("UI/GameLabel.json"), new TextureAtlas("UI/Ribbons.atlas"));

        // Creating labels for wood, food and gold
        woodlabel = new Label("Wood" + "\n" + Wood.WOOD, skin, "blue");
        foodLabel = new Label("Food: " + Wood.WOOD, skin, "red");
        goldLabel = new Label("Gold: " + Wood.WOOD, skin, "gold");

        // Scaling the FONT SIZE
        woodlabel.setFontScale(.7f);
        foodLabel.setFontScale(.7f);
        goldLabel.setFontScale(.7f);

        // Center Aligning the text
        woodlabel.setAlignment(Align.center, Align.center);
        foodLabel.setAlignment(Align.center, Align.center);
        goldLabel.setAlignment(Align.center, Align.center);

        // ADD actors to the table
        table.top().left();
        table.setFillParent(true);
        table.add(woodlabel).pad(20, 20, 10, 10);
        table.add(foodLabel).pad(20, 20, 10, 10);
        table.add(goldLabel).pad(20, 20, 10, 10);
//        stage.setDebugAll(true); // For Debugging
        stage.addActor(table);
    }

    public void updateLabelTexts() {
        woodlabel.setText("Wood: " + Wood.WOOD);
    }
}
