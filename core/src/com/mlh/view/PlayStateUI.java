/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.game.HMSDreadnought;

/**
 *
 * @author michael
 */
public class PlayStateUI extends UI {
    
    public static final String PERCENT_SIGN = "%";

    public static final String STR_SCORE =  "SCORE:  ";
    public static final String STR_HEALTH = "HEALTH: ";
    public static final String STR_SHIELD = "SHIELD: ";
    
    public static final String STR_WPM =  "WPM:  ";
    public static final String STR_LWPM = "LWPM: ";
    public static final String STR_SWPM = "SWPM: ";
    public static final String STR_ENEMY_COUNT = "EC:   ";
    
    private static final int X_START = 0;
    private static final int X_OFFSET = 20;
    private static final int Y_OFFSET = 20;
    private static final int WPM_X_OFFSET = 150;

    private final BitmapFont font;

    private int wpm;
    private int lwpm;
    private int swpm;
    private long score;
    private float health;
    private float shield;
    private int enemyCount;

    private float[] scorePos;
    private float[] healthPos;
    private float[] shieldPos;
    private float[] wpmPos;
    private float[] lwpmPos;
    private float[] swpmPos;
    private float[] enemyCountPos;

    private boolean drawWpmOn = false;

    public PlayStateUI(BitmapFont font) {
        this.font = font;
        this.scorePos = new float[]{X_START + X_OFFSET, HMSDreadnought.HEIGHT - Y_OFFSET};
        this.healthPos = new float[]{scorePos[0], scorePos[1] - Y_OFFSET};
        this.shieldPos = new float[] {healthPos[0], healthPos[1] - Y_OFFSET};

        this.wpmPos = new float[]{HMSDreadnought.WIDTH - WPM_X_OFFSET, HMSDreadnought.HEIGHT - Y_OFFSET};
        this.lwpmPos = new float[] {wpmPos[0], wpmPos[1] - Y_OFFSET};
        this.swpmPos = new float[] {lwpmPos[0], lwpmPos[1] - Y_OFFSET};              
        this.enemyCountPos = new float[] {swpmPos[0], swpmPos[1] - Y_OFFSET};

    }

    public void setAll(long score, float health, float shield, int wpm, int lwpm, int swpm, int enemyCount) {
        this.score = score;
        this.health = health;
        this.shield = shield;
        this.wpm = wpm;
        this.lwpm = lwpm;
        this.swpm = swpm;
        this.enemyCount = enemyCount;
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.begin();
        drawScore(sb);
        drawHealth(sb);
        drawSheild(sb);
        drawWpm(sb);

        sb.end();
    }

    private void drawWpm(SpriteBatch sb) {
        if (drawWpmOn) {
            font.draw(sb, STR_WPM + Long.toString(wpm), wpmPos[0], wpmPos[1]);
            font.draw(sb, STR_LWPM + Long.toString(lwpm), lwpmPos[0], lwpmPos[1]);
            font.draw(sb, STR_SWPM + Long.toString(swpm), swpmPos[0], swpmPos[1]);
            font.draw(sb, STR_ENEMY_COUNT + Integer.toString(enemyCount), enemyCountPos[0], enemyCountPos[1]);
        }
    }

    private void drawScore(SpriteBatch sb) {
        font.draw(sb, STR_SCORE + Long.toString(score), scorePos[0], scorePos[1]);
    }

    private void drawHealth(SpriteBatch sb) {
        font.draw(sb, STR_HEALTH + Integer.toString((int)health) + PERCENT_SIGN, healthPos[0], healthPos[1]);
    }

    private void drawSheild(SpriteBatch sb) {
        font.draw(sb, STR_SHIELD + Integer.toString((int)shield) + PERCENT_SIGN, shieldPos[0], shieldPos[1]);
    }

    public void setWpm(int wpm) {
        this.wpm = wpm;
    }

    public void setLwpm(int lwpm) {
        this.lwpm = lwpm;
    }

    public void setSwpm(int swpm) {
        this.swpm = swpm;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setShield(float shield) {
        this.shield = shield;
    }

    
    public boolean isDrawWpmOn() {
        return drawWpmOn;
    }

    public void setDrawWpmOn(boolean drawWpmOn) {
        this.drawWpmOn = drawWpmOn;
    }

}
