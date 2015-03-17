/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.utilities;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author michael
 */
public class MatrixUtils {

    public static Vector2 transformVector2s(Matrix3 mat3, Vector2 v2) {
        Vector2 result = new Vector2();
        result.x = (mat3.val[Matrix3.M00] * v2.x) + (mat3.val[Matrix3.M10] * v2.y) + (mat3.val[Matrix3.M20]);
        result.y = (mat3.val[Matrix3.M01] * v2.x) + (mat3.val[Matrix3.M11] * v2.y) + (mat3.val[Matrix3.M21]);

        return result;
    }

    //create a rotation matrix from a 2D vector
    public static Matrix3 rotate(Vector2 fwd, Vector2 side) {
        Matrix3 mat = new Matrix3();

        mat.val[Matrix3.M00] = fwd.x;
        mat.val[Matrix3.M01] = fwd.y;
        mat.val[Matrix3.M02] = 0;
        
        mat.val[Matrix3.M10] = side.x;
        mat.val[Matrix3.M11] = side.y;
        mat.val[Matrix3.M12] = 0;
        
        mat.val[Matrix3.M20] = 0;
        mat.val[Matrix3.M21] = 0;
        mat.val[Matrix3.M22] = 1;

        //and multiply
        mat.mul(mat);
        
        return mat;
    }
}
