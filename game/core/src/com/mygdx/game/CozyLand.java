package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.screens.Splash;

public class CozyLand extends Game {

	// Game title
	public  static final String TITLE = "Cozy Land", VERSION = "0.0.0.0.ReallyReallyEarly";

    @Override
	public void create() {
		setScreen(new Splash());
	}

	@Override
	public void render() {
			super.render();
	}

	@Override
	public void resize(int w, int h) {
		super.resize(w, h);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
