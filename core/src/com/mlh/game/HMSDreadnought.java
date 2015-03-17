package com.mlh.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mlh.gamestates.MenuState;
import com.mlh.managers.*;
import com.mlh.models.ScoreData;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMSDreadnought extends ApplicationAdapter {
	// public game const
	public static final float WEAPON_STAGERED_FIRE_MIN = 0.5f;
	public static final float WEAPON_STAGERED_FIRE_MAX = 1.0f;

	public static final int VERY_EASY = 15;
	public static final int EASY = 30;
	public static final int MEDIUM = 50;
	public static final int HARD = 75;
	public static final int VERY_HARD = 100;
	public static final int INSANE = 125;
	public static final int IMPOSSIBLE = 175;

	public static boolean demo = false;


	public static float phraseTimer = 0.1f;
	public static int wpm = 15;
	public static int wordLength = 5;

	private static float healForTyping = 1;

	public static float getHealForTyping() {
		return healForTyping;
	}

	public static void setHealForTyping(float _healForTyping) {
		healForTyping = _healForTyping;
	}




	//SpriteBatch batch;
	//Texture img;

	public static int WIDTH;
	public static int HEIGHT;
	public static String TITLE;
	public static OrthographicCamera camera;
	private GameStateManager gsm;
	private static ScoreData scoreData;
	public static final String BACKGROUNDS = "core/assets/data/backgrounds.ser";
	public static final String HIGHSCORES = "highscores.sav";

	@Override
	public void create() {

		//batch = new SpriteBatch();
		//img = new Texture("badlogic.jpg");
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		TITLE = "H.M.S. Dreadnought";

		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.translate(WIDTH / 2, HEIGHT / 2);
		camera.update();

		Gdx.input.setInputProcessor(
				new GameInputProcessor()
		);

		// *** INIT *** //
		// load assets/data
		loadScores();
		//BackGroundManager.load(BACKGROUNDS);
		BackGroundManager.init();
		BackGroundManager.add("nebula", "core/assets/textures/backgrounds/nebula.jpg");
		BackGroundManager.add("blueCloud", "core/assets/textures/backgrounds/blueCloud.png");
		BackGroundManager.add("stars", "core/assets/textures/backgrounds/stars.png");
		BackGroundManager.add("spacefield", "core/assets/textures/backgrounds/spacefield.png");
		BackGroundManager.add("redGreenClouds", "core/assets/textures/backgrounds/redGreenClouds.png");
		//BackGroundManager.save(BACKGROUNDS);

		Jukebox.load("core/assets/sounds/explode.ogg", "explode");
		Jukebox.load("core/assets/sounds/extralife.ogg", "extralife");
		Jukebox.load("core/assets/sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("core/assets/sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("core/assets/sounds/pulselow.ogg", "pulselow");
		Jukebox.load("core/assets/sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("core/assets/sounds/shoot.ogg", "shoot");
		Jukebox.load("core/assets/sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("core/assets/sounds/thruster.ogg", "thruster");
		Jukebox.load("core/assets/sounds/Laser_Cannon.ogg", "laserCannon");
		Jukebox.load("core/assets/sounds/laserHit.ogg", "laserHit");

		// one time code
        /*
        try {
            BackGroundManager.init();
            BackGroundManager.add("blueCloud", "core/assets/textures/backgrounds/blueCloud.png");
            BackGroundManager.add("nebula", "core/assets/textures/backgrounds/nebula.jpg");
            BackGroundManager.add("redGreenClouds", "core/assets/textures/backgrounds/redGreenClouds.png");
            BackGroundManager.add("spacefield", "core/assets/textures/backgrounds/spacefield.png");
            BackGroundManager.add("stars", "core/assets/textures/backgrounds/stars.png");
            BackGroundManager.save(BACKGROUNDS);
        } catch (Exception ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
		// start
		gsm = new GameStateManager();
	}

	/**
	 * Returns reference to ScoreData object
	 *
	 * @return ScoreData
	 */
	public static ScoreData getScoreData() {
		return scoreData;
	}

	/**
	 * Set ScoreData object
	 *
	 * @param _scoreData
	 */
	public static void setScoreData(ScoreData _scoreData) {
		scoreData = _scoreData;
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.begin();
		//batch.draw(img, 0, 0);
		//batch.end();

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();

		GameKeys.update();
	}

	private static void loadScores() {

		if (!(new File(HIGHSCORES).exists())){
			scoreData = new ScoreData();
			scoreData.init();
			saveScores();
			return;
		}

		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(HIGHSCORES));
			scoreData = (ScoreData) in.readObject();
		} catch (IOException ex) {
			Logger.getLogger(MenuState.class.getName()).log(Level.SEVERE, null, ex);
			Gdx.app.exit();
		} catch (ClassNotFoundException ex) {
			scoreData = new ScoreData();
			scoreData.init();
			saveScores();
		}
	}

	public static void saveScores() {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(
					new FileOutputStream(HIGHSCORES)
			);
			out.writeObject(scoreData);
			out.close();
		} catch (IOException ex) {
			Logger.getLogger(HMSDreadnought.class.getName()).log(Level.SEVERE, null, ex);
			Gdx.app.exit();
		}
	}
    /* (todo) May not need these
     @Override
     public void resize(int width, int height) {}
     @Override
     public void pause() {}
     @Override
     public void resume() {}
     */

	@Override
	public void dispose() {
		saveScores();
	}
}