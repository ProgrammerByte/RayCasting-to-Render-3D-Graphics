package com.mygdx.game.desktop;

import java.awt.Window;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.RayTracing;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Ray Casting Demo";
		new LwjglApplication(new RayTracing(), cfg);
	}
}
