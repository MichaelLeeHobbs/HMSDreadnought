/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.utilities;

/**
 *
 * @author michael
 */
public class MathUtilities {
    public static float clampDeg(float deg){
        return (deg % 360 < 0) ? (deg % 360) + 360 : deg % 360;
    }
    
}
