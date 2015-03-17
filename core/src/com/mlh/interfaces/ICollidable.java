/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author michael
 */
public interface ICollidable {
    
    /**
     * Returns an array of rectangles that are used for collision detection
     * @return
     */
    Rectangle[] getCollisionBoxes();
    
    /**
     * Tell this box collide with other
     * @param box
     * @param other
     */
    public void collision(int box, ICollidable other);
    
    /**
     * Get name of this
     * @return
     */
    public String getName();
    
    /**
     * Damage a target should take when they collide with this.
     * @return int
     */
    public int getCollisionValue();
    
    /**
     * Return the parent of this. Used to deal with collision from self generated objects.
     * @return
     */
    public Object getParent();
    
    public float getBoundingRadius();
    public boolean isTagged();
    public void setTagged(boolean tagged);
    public Vector2 getOrigin();
    public String getType();
    public boolean shouldRemove();
}

