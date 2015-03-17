/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.actors.Asteroid;
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.*;
import com.mlh.models.World;
import com.mlh.models.levels.*;

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class MenuState extends GameState implements GameInputListener {

    // protected GameKeys gameKeys from GameState
    private SpriteBatch sb;
    private ShapeRenderer sr;
    private BitmapFont titleFont;
    private BitmapFont font;

    private int currentItem;
    private String[] menuItems;

    private ArrayList<Asteroid> asteroids;
    // world
    private World world;

    private Sprite backGroundSprite;

    public MenuState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
        gameInputProcessor.addListener(this);
    }

    @Override
    public void init() {
        // world
        world = new World(HMSDreadnought.WIDTH, HMSDreadnought.HEIGHT, (int) (HMSDreadnought.WIDTH * 0.5f), (int) (HMSDreadnought.HEIGHT * 0.5f));

        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        PhraseManager phraseManager = new PhraseManager();

        // back ground
        String backGround = "redGreenClouds";
        backGroundSprite = BackGroundManager.get(backGround);
        backGroundSprite.setSize(HMSDreadnought.WIDTH, HMSDreadnought.HEIGHT);

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        titleFont = gen.generateFont(parameter);

        titleFont.setColor(Color.WHITE);

        parameter.size = 20;
        font = gen.generateFont(parameter);

        menuItems = new String[]{
            "Play Level 1",
            "Play Level 2",
            "Play Level 3",
            "Play Level 4",
            "Play Level 5",
            "Play - Free Play",
            "Highscores",
            "Quit",
            "Set Difficulty"
        };
        /*
         for (int i = 0; i < 6; i++) {
         world.addObject(
         new Asteroid("menuAsteroid", null, 1, world, 5, "",
         phraseManager,
         null,
         Asteroid.LARGE
         )
         );
         }
         */
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.update(dt);
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(HMSDreadnought.camera.combined);
        sr.setProjectionMatrix(HMSDreadnought.camera.combined);

        sb.begin();
        // draw background first always
        backGroundSprite.draw(sb);
        // draw title
        float width = titleFont.getBounds(HMSDreadnought.TITLE).width;
        titleFont.setColor(Color.MAROON);
        titleFont.draw(sb, HMSDreadnought.TITLE, (HMSDreadnought.WIDTH - width) / 2, HMSDreadnought.HEIGHT - 200);

        // draw menu
        for (int i = 0; i < menuItems.length; i++) {
            width = font.getBounds(menuItems[i]).width;
            if (currentItem == i) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.WHITE);
            }
            font.draw(sb, menuItems[i], (HMSDreadnought.WIDTH - width) / 2, 400 - 35 * i);
        }

        sb.end();

        world.draw(sr, sb);
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.UP)) {
            if (currentItem > 0) {
                currentItem--;
            }
        }
        if (GameKeys.isPressed(GameKeys.DOWN)) {
            if (currentItem < menuItems.length) {
                currentItem++;
            }
        }
        if (GameKeys.isPressed(GameKeys.ENTER)) {
            select();
        }
    }

    private void select() {
        switch (currentItem) {
            case 0:
                gsm.setState(GameStateManager.PLAY, new Level001());
                break;
            case 1:
                gsm.setState(GameStateManager.PLAY, new Level002());
                break;
            case 2:
                gsm.setState(GameStateManager.PLAY, new Level003());
                break;
            case 3:
                gsm.setState(GameStateManager.PLAY, new Level004());
                break;
            case 4:
                gsm.setState(GameStateManager.PLAY, new Level005());
                break;
            case 5:
                gsm.setState(GameStateManager.PLAY, new LevelFreePlay());
                break;
            case 6:
                gsm.setState(GameStateManager.HIGHSCORE);
                break;
            case 7:
                Gdx.app.exit();
                break;
            case 8:
                gsm.setState(GameStateManager.DIFFICULTY_SELECT);
                break;
            case 9:
                HMSDreadnought.demo = !HMSDreadnought.demo;
                System.out.println("demo = " + HMSDreadnought.demo);
                break;
        }

    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        titleFont.dispose();
        font.dispose();
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

}
