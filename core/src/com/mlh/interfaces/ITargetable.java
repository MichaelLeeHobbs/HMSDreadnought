/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.interfaces;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author michael
 */
public interface ITargetable {
    public Vector2 getPosition();
    public Vector2 getVelocity();
    public Vector2 getOrigin();
    public String getName();
    public boolean isValid();
    public float getHealth();
    public float getShield();
}
