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
public interface IMoveable {
    public Vector2 getPosition();
    public Vector2 getVelocity();
    public String getName();
}
