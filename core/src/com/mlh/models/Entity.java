/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.IUpdateable;

/**
 * Base class for game objects
 *
 * @author michael
 */
public abstract class Entity implements IUpdateable {

    protected String name;
    protected Object owner;
    protected World world;
    protected final Vector2 position;
    protected boolean remove;
    protected int boundryResponce;
    protected boolean tagged;

    public Entity() {
        this("null", null, null);
    }

    public Entity(String name, Object owner, World world) {
        this.name = name;
        this.owner = owner;
        this.world = world;
        this.remove = false;
        this.boundryResponce = World.BOUNDRY_REMOVE;
        this.position = new Vector2();
        this.tagged = false;
    }

    public void init(String name, Object owner, World world) {
        this.name = name;
        this.owner = owner;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public Object getParent() {
        return owner;
    }

    @Override
    public boolean shouldRemove() {
        return remove;
    }

    public void remove() {
        remove = true;
    }

    public int getBoundryResponce() {
        return boundryResponce;
    }

    public void setBoundryResponce(int boundryResponce) {
        this.boundryResponce = boundryResponce;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Vector2 getPosition() {
        return position.cpy();
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public World getWorld() {
        return world;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }
}
