/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

/**
 *
 * @author michael
 */
public enum Deceleration {
    SLOW(3), NORMAL(2), FAST(1);
    
    private final int value;
    
    private Deceleration(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
}
