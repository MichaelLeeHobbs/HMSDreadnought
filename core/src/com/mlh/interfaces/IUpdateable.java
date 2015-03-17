/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.interfaces;

/**
 *
 * @author michael
 */
public interface IUpdateable {
    public void update(float dt);
    public boolean shouldRemove();
}
