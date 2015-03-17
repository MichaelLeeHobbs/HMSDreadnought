package com.mlh.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mlh.game.HMSDreadnought;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "HMS Dreadnought";
		config.width = 1280;
		config.height = 1024;
		config.useGL30 = false;
		config.resizable = true;

		new LwjglApplication(new HMSDreadnought(), config);
	}
}
