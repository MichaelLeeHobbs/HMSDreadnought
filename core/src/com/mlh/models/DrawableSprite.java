/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.interfaces.IDrawable;

/**
 *
 * @author michael
 */
public class DrawableSprite extends Entity implements IDrawable {
    
    private int layer = 1;
    private Sprite sprite;

    public DrawableSprite(String name, Object parent, World world, Sprite sprite, int drawLayer) {
        super(name, parent, world);
        this.position.set(sprite.getX(), sprite.getY());
        this.sprite = sprite;
        this.layer = drawLayer;
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        sb.begin();
        sprite.draw(sb);
        sb.end();
    }

    @Override
    public int getDrawLayer() {
        return layer;
    }
    
    public void setDrawLayer(int layer) {
        this.layer = layer;
    }

    @Override
    public void update(float dt) {
        // do nothing
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
}
