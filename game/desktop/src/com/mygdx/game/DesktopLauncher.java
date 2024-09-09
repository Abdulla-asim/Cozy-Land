package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle(CozyLand.TITLE + " v" + CozyLand.VERSION);
		config.useVsync(true);
//		config.setWindowedMode(1920, 1280); // Custom screen size
		config.setMaximized(true); // To render a maximized screen
		new Lwjgl3Application(new CozyLand(), config);
	}
}
