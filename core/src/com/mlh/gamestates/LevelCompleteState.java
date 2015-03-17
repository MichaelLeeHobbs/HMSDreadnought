/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.GameInputProcessor;
import com.mlh.managers.GameKeys;
import com.mlh.managers.GameStateManager;
import com.mlh.models.LevelCompleteData;

/**
 *
 * @author michael
 */
public class LevelCompleteState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private BitmapFont levelCompleteFont;
    private BitmapFont font;

    public LevelCompleteState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        levelCompleteFont = gen.generateFont(parameter);
        parameter.size = 20;
        font = gen.generateFont(parameter);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(HMSDreadnought.camera.combined);
        sr.setProjectionMatrix(HMSDreadnought.camera.combined);
        sb.begin();
        String s;
        float w;

        s = "Level Complete";
        w = levelCompleteFont.getBounds(s).width;
        levelCompleteFont.draw(sb, s, HMSDreadnought.WIDTH / 3, HMSDreadnought.HEIGHT / 2 - 0);

        s = "Score: " + ((LevelCompleteData) data).getScore();
        w = levelCompleteFont.getBounds(s).width;
        levelCompleteFont.draw(sb, s, HMSDreadnought.WIDTH / 3, HMSDreadnought.HEIGHT / 2 - 40);

        s = "WPM: " + ((LevelCompleteData) data).getWpm();
        w = levelCompleteFont.getBounds(s).width;
        levelCompleteFont.draw(sb, s, HMSDreadnought.WIDTH / 3, HMSDreadnought.HEIGHT / 2 - 80);

        s = "Press enter to continue...";
        w = levelCompleteFont.getBounds(s).width;
        levelCompleteFont.draw(sb, s, HMSDreadnought.WIDTH / 3, HMSDreadnought.HEIGHT / 2 - 120);
        
        s = "Escape to exit to the menu...";
        w = levelCompleteFont.getBounds(s).width;
        levelCompleteFont.draw(sb, s, HMSDreadnought.WIDTH / 3, HMSDreadnought.HEIGHT / 2 - 160);

        sb.end();

    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ENTER)) {
            gsm.setState(GameStateManager.PLAY, ((LevelCompleteData) data).getNextLevel());
        }

        if (GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.MENU);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        levelCompleteFont.dispose();
        font.dispose();
    }

}
