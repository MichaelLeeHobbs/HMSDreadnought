/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author michael
 */
public class GameAnimation {

    private final int frameCols;
    private final int frameRows;
    private final float frameTime;

    private Animation animation;
    private boolean loop;
    private boolean done;

    private TextureRegion currentFrame;
    private float stateTime;
    private final float animationTime;

    private final String textureFile;
    
    private float x;
    private float y;

    public GameAnimation(String textureFile, int frameCols, int frameRows, float frameTime, boolean loop) {
        this.textureFile = textureFile;
        this.frameCols = frameCols;
        this.frameRows = frameRows;
        this.frameTime = frameTime;
        this.animationTime = frameCols * frameRows * frameTime;
        this.loop = loop;
        this.done = false;
    }

    public void init() {
        Texture sheet = new Texture(Gdx.files.internal(textureFile));
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frameCols, sheet.getHeight() / frameRows);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];

        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        animation = new Animation(frameTime, frames);
        stateTime = 0f;
    }

    public void update(float dt) {
        if (!done && !loop && stateTime > animationTime) {
            done = true;
            return;
        }
        stateTime += dt;
        currentFrame = animation.getKeyFrame(stateTime, loop);
    }
    
    public void draw(SpriteBatch spriteBatch) {
        if (currentFrame == null) {
            return;
        }
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, x - (currentFrame.getRegionWidth() / 2), y - (currentFrame.getRegionHeight() / 2));
        spriteBatch.end();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setPosition(Vector2 position){
        this.x = position.x;
        this.y = position.y;
    }

    public boolean isDone() {
        return done;
    }
    
}
