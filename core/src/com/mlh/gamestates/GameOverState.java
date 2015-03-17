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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.mlh.game.HMSDreadnought;
import com.mlh.managers.GameInputProcessor;
import com.mlh.managers.GameKeys;
import com.mlh.managers.GameStateManager;

/**
 *
 * @author michael
 */
public class GameOverState extends GameState {

    private SpriteBatch sb;
    private ShapeRenderer sr;

    private boolean newHighScore;
    private char[] newName;
    private int currentChar;

    private BitmapFont gameOverFont;
    private BitmapFont font;

    public GameOverState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
    }

    @Override
    public void init() {
        sb = new SpriteBatch();
        sr = new ShapeRenderer();
        newHighScore = HMSDreadnought.getScoreData().isHighScore((Long)data);
        if (newHighScore) {
            HMSDreadnought.getScoreData().setTentativeScore((Long)data);
        }
        System.out.println("score = " + newHighScore);
        if (newHighScore) {
            newName = new char[]{'A', 'A', 'A'};
        }

        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        gameOverFont = gen.generateFont(parameter);
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

        s = "Game Over";
        w = gameOverFont.getBounds(s).width;
        gameOverFont.draw(sb, s, (HMSDreadnought.WIDTH - 2) / 2, 220);

        if (!newHighScore) {
            sb.end();
            return;
        }

        s = "New High Score: " + HMSDreadnought.getScoreData().getTentativeScore();
        w = gameOverFont.getBounds(s).width;
        gameOverFont.draw(sb, s, (HMSDreadnought.WIDTH - 2) / 2, 180);

        for (int i = 0; i < newName.length; i++) {
            font.draw(
                    sb,
                    Character.toString(newName[i]),
                    230 + 14 * i,
                    120
            );
        }

        sb.end();

        sr.begin(ShapeType.Line);
        sr.line(
                230 + 14 * currentChar,
                100,
                244 + 14 * currentChar,
                100
        );
        sr.end();
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ENTER)) {
            if (newHighScore) {
                HMSDreadnought.getScoreData().addHighScore(HMSDreadnought.getScoreData().getTentativeScore(), new String(newName));
                HMSDreadnought.saveScores();
            }
            gsm.setState(GameStateManager.MENU);
        }
        if (newHighScore) {

            if (GameKeys.isPressed(GameKeys.UP)) {
                if (newName[currentChar] == ' ') {
                    newName[currentChar] = 'Z';
                } else {
                    newName[currentChar]--;
                    if (newName[currentChar] < 'A') {
                        newName[currentChar] = ' ';
                    }
                }
            }

            if (GameKeys.isPressed(GameKeys.DOWN)) {
                if (newName[currentChar] == ' ') {
                    newName[currentChar] = 'A';
                } else {
                    newName[currentChar]++;
                    if (newName[currentChar] > 'Z') {
                        newName[currentChar] = ' ';
                    }
                }
            }
            if (GameKeys.isPressed(GameKeys.RIGHT)) {
                if (currentChar < newName.length - 1) {
                    currentChar++;
                }
            }
            if (GameKeys.isPressed(GameKeys.LEFT)) {
                if (currentChar > 0) {
                    currentChar--;
                }
            }
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();
    }

}
