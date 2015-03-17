/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.IDrawable;

/**
 *
 * @author michael
 */
public class Particle extends Entity implements IDrawable {

    private int width;
    private float timer;
    private float time;
    Vector2 velocity;
    int drawLayer;

    public Particle(World world, float x, float y) {
        super("particle", null, world);
        velocity = new Vector2();
        position.set(x, y);
        int height;
        width = height = 2;
        drawLayer = 1;

        float speed = 50;
        float facing = MathUtils.random(2 * 3.1415f);
        velocity.x = MathUtils.cos(facing) * speed;
        velocity.y = MathUtils.sin(facing) * speed;

        timer = 0;
        time = 1;
    }

    @Override
    public void update(float dt) {
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        timer += dt;
        if (timer > time) {
            remove = true;
        }
    }

    @Override
    public void draw(ShapeRenderer sr, SpriteBatch sb) {
        sr.setColor(MathUtils.random(1), MathUtils.random(1), MathUtils.random(1), 1);
        sr.begin(ShapeType.Filled);
        sr.circle(position.x - width / 2, position.y - width / 2, width / 2);
        sr.end();
    }

    @Override
    public int getDrawLayer() {
        return drawLayer;
    }
}
