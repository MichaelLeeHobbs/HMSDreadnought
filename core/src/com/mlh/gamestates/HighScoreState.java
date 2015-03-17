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
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.GameInputProcessor;
import com.mlh.managers.GameKeys;
import com.mlh.managers.GameStateManager;

/**
 *
 * @author michael
 */
public class HighScoreState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;
    private long[] highScores;
    private String[] names;

    public HighScoreState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
    }



    @Override
    public void init() {
        sb = new SpriteBatch();

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);
        //font.setColor(Color.WHITE);

        highScores = HMSDreadnought.getScoreData().getHighScores();
        names = HMSDreadnought.getScoreData().getNames();
    }

    @Override
    public void update(float dt) {
        handleInput();

    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(HMSDreadnought.camera.combined);

        sb.begin();
        String s;
        float w;
        s = "High Scores";
        w = font.getBounds(s).width;
        font.draw(sb, s, (HMSDreadnought.WIDTH - w) / 2, 300);

        for (int i = 0; i < highScores.length; i++) {
            s = String.format(
                    "%2d, %7s %s",
                    i + 1,
                    highScores[i],
                    names[i]
            );
            w = font.getBounds(s).width;
            font.draw(sb, s, (HMSDreadnought.WIDTH - w) / 2, 270 - 20 * i);
            font.draw(sb, s, (HMSDreadnought.WIDTH - w) / 2, 270 - 20 * i);
        }

        sb.end();

    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ENTER)
                || GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.MENU);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        font.dispose();

    }

}
