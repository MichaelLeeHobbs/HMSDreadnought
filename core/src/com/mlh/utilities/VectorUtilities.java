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
public class VectorUtilities {

    public static final int CLOCKWISE = 1;
    public static final int ANTICLOCKWISE = -1;

    public static Vector2 rotatePointAroundPoint(Vector2 point, Vector2 rotatePoint, float angle) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        return new Vector2(
                cosAngle * (point.x - rotatePoint.x) - sinAngle * (rotatePoint.y - point.y) + rotatePoint.x,
                sinAngle * (point.x - rotatePoint.x) - cosAngle * (rotatePoint.y - point.y) + rotatePoint.y
        );
    }

    public static Vector2 vectorToTarget(Vector2 start, Vector2 targetOrigin, Vector2 targetVelocity, float speed) {

        Vector2 dirToTarget = targetOrigin.cpy().sub(start).nor();
        Vector2 targetVelOrth = new Vector2().mulAdd(dirToTarget, Vector2.dot(targetVelocity.x, targetVelocity.y, dirToTarget.x, dirToTarget.y));
        Vector2 targetVelTang = targetVelocity.cpy().sub(targetVelOrth);
        Vector2 shotVelTang = targetVelTang.cpy();

        float shotVelSpeed = shotVelTang.len();
        if (shotVelSpeed > speed) {
            return targetVelocity.cpy().nor().scl(speed);
        } else {
            float shotSpeedOrth = (float) Math.sqrt(speed * speed - shotVelSpeed * shotVelSpeed);
            Vector2 shotVelOrth = new Vector2().mulAdd(dirToTarget, shotSpeedOrth);
            return shotVelOrth.add(shotVelTang);
        }

        //return shotVelOrth.add(shotVelTang);
    }

    /**
     * returns positive if v2 is clockwise of this v1, negative if anticlockwise
     * (assuming the Y axis is pointing down, X axis to right like a Window app)
     *
     * @param v1
     * @param v2
     * @return
     */
    public static int sign(Vector2 v1, Vector2 v2) {
        if (v1.y * v2.x > v1.x * v2.y) {
            return ANTICLOCKWISE;
        } else {
            return CLOCKWISE;
        }
    }

    /**
     * returns the vector that is perpendicular to this one.
     */
    public static Vector2 perpendicular(Vector2 v) {
        return new Vector2(-v.y, v.x);
    }

    //-------------------------- Vec2DRotateAroundOrigin --------------------------
//
//  rotates a vector ang rads around the origin
//-----------------------------------------------------------------------------
    public static Vector2 vec2RotateAroundOrigin(Vector2 v, float ang) {
        //create a transformation matrix
        Matrix3 mat = new Matrix3();

        //rotate
        mat.rotate(ang);

        //now transform the object's vertices
        return MatrixUtils.transformVector2s(mat, v);
    }

    //--------------------- PointToWorldSpace --------------------------------
//
//  Transforms a point from the agent's local space into world space
//------------------------------------------------------------------------
    public static Vector2 pointToWorldSpace(Vector2 point,
            Vector2 AgentHeading,
            Vector2 AgentSide,
            Vector2 AgentPosition) {
        //make a copy of the point
        Vector2 transPoint = point.cpy();

        //create a transformation matrix
        Matrix3 matTransform;

        //rotate
        matTransform = MatrixUtils.rotate(AgentHeading, AgentSide);

        //and translate
        matTransform.translate(AgentPosition.x, AgentPosition.y);

        //now transform the vertices
        transPoint = MatrixUtils.transformVector2s(matTransform, transPoint);

        return transPoint;
    }

    //--------------------- PointToLocalSpace --------------------------------
//
//------------------------------------------------------------------------
    public static Vector2 pointToLocalSpace(Vector2 point,
            Vector2 AgentHeading,
            Vector2 AgentSide,
            Vector2 AgentPosition) {

        //make a copy of the point
        Vector2 TransPoint = new Vector2(point);

        //create a transformation matrix
        Matrix3 matTransform = new Matrix3();

        float Tx = -AgentPosition.dot(AgentHeading);
        float Ty = -AgentPosition.dot(AgentSide);

        //create the transformation matrix
        matTransform.val[Matrix3.M00] = AgentHeading.x;
        matTransform.val[Matrix3.M01] = AgentSide.x;
        matTransform.val[Matrix3.M10] = AgentHeading.y;
        matTransform.val[Matrix3.M11] = AgentSide.y;
        matTransform.val[Matrix3.M20] = Tx;
        matTransform.val[Matrix3.M21] = Ty;

        //now transform the vertices
        //matTransform.transformVector2s(TransPoint);
        TransPoint = MatrixUtils.transformVector2s(matTransform, TransPoint);

        return TransPoint;
    }

    //--------------------- VectorToWorldSpace --------------------------------
//
//  Transforms a vector from the agent's local space into world space
//------------------------------------------------------------------------
    public static Vector2 VectorToWorldSpace(Vector2 vec,
            Vector2 AgentHeading,
            Vector2 AgentSide) {
        //make a copy of the point
        Vector2 TransVec = new Vector2(vec);

        //create a transformation matrix
        Matrix3 matTransform;

        //rotate
        matTransform = MatrixUtils.rotate(AgentHeading, AgentSide);

        //now transform the vertices
        TransVec = MatrixUtils.transformVector2s(matTransform, TransVec);

        return TransVec;
    }

    
}
