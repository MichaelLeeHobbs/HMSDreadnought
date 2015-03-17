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

import java.util.ArrayList;

/**
 *
 * @author michael
 */
public class MenuSelectDifficultyState extends GameState implements GameInputListener {

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

    public MenuSelectDifficultyState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
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
            "Very Easy",
            "Easy",
            "Medium",
            "Hard",
            "Very Hard",
            "Insane",
            "Impossible"
        };
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
                HMSDreadnought.wpm = HMSDreadnought.VERY_EASY;
                HMSDreadnought.wordLength = 5;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 1:
                HMSDreadnought.wpm = HMSDreadnought.EASY;
                HMSDreadnought.wordLength = 6;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 2:
                HMSDreadnought.wpm = HMSDreadnought.MEDIUM;
                HMSDreadnought.wordLength = 7;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 3:
                HMSDreadnought.wpm = HMSDreadnought.HARD;
                HMSDreadnought.wordLength = 8;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 4:
                HMSDreadnought.wpm = HMSDreadnought.VERY_HARD;
                HMSDreadnought.wordLength = 9;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 5:
                HMSDreadnought.wpm = HMSDreadnought.INSANE;
                HMSDreadnought.wordLength = 10;
                gsm.setState(GameStateManager.MENU, null);
                break;
            case 6:
                HMSDreadnought.wpm = HMSDreadnought.IMPOSSIBLE;
                HMSDreadnought.wordLength = 15;
                gsm.setState(GameStateManager.MENU, null);
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
