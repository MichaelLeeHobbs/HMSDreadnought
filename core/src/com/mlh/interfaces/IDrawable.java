/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 *
 * @author michael
 */
public interface IDrawable {
    public void draw(ShapeRenderer sr, SpriteBatch sb);
    public int getDrawLayer();
    public boolean shouldRemove();
}
