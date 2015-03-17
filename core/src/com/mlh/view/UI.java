/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mlh.interfaces.IDrawable;

/**
 *
 * @author michael
 */
public abstract class UI implements IDrawable {
    protected int drawLayer = 0;
    protected boolean shouldRemove = false;

    @Override
    public abstract void draw(ShapeRenderer sr, SpriteBatch sb);

    @Override
    public int getDrawLayer() {
        return drawLayer;
    }

    public void setDrawLayer(int drawLayer) {
        this.drawLayer = drawLayer;
    }

    @Override
    public boolean shouldRemove() {
        return shouldRemove;
    }

    public void setShouldRemove(boolean shouldRemove) {
        this.shouldRemove = shouldRemove;
    }


}
