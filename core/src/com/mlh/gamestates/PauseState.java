/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
public class PauseState extends GameState {

    private SpriteBatch sb;
    private BitmapFont font;

    public PauseState(GameStateManager gsm, GameInputProcessor gameInputProcessor, Object data) {
        super(gsm, gameInputProcessor, data);
    }


    @Override
    public void init() {
        boolean isPaused = true;
        sb = new SpriteBatch();
        
        // set font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("core/assets/fonts/Hyperspace Bold.ttf")
        );
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;
        font = gen.generateFont(parameter);
        font.setColor(Color.WHITE);

    }

    @Override
    public void update(float dt) {
        handleInput();

    }

    @Override
    public void draw() {
        sb.setProjectionMatrix(HMSDreadnought.camera.combined);
    
        sb.begin();
        // draw paused
        String paused = "PAUSED";
        float width = font.getBounds(paused).width;
        font.draw(sb, paused, (HMSDreadnought.WIDTH - width) / 2, 300);

        sb.end();
    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.ESCAPE)) {
            gsm.setState(GameStateManager.PLAY);
        }
    }

    @Override
    public void dispose() {
        sb.dispose();
        font.dispose();
    }

}
