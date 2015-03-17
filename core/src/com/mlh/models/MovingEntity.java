/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.mlh.interfaces.ICollidable;
import com.mlh.utilities.VectorUtilities;

import static com.mlh.utilities.MatrixUtils.transformVector2s;
import static com.mlh.utilities.VectorUtilities.perpendicular;

public abstract class MovingEntity extends Entity implements ICollidable {

    private static final float MINIMAL_TURN_ANGLE = 0.00001f;
    private String type;


    @SuppressWarnings("ProtectedField")
    protected final Vector2 velocity;
    //a normalized vector pointing in the direction the entity is heading. 
    protected final Vector2 heading;
    //a vector perpendicular to the heading vector
    protected final Vector2 side;
    protected float mass;
    //the maximum speed this entity may travel at.
    protected float maxSpeed;
    //the maximum force this entity can produce to power itself 
    //(think rockets and thrust)
    protected float maxForce;
    //the maximum rate (radians per second)this vehicle can rotate         
    protected float maxTurnRate;

    public MovingEntity() {
        this("null", null, null);
    }

    public MovingEntity(String name, Object owner, World world) {
        this("null",
                null,
                null,
                new Vector2(),
                new Vector2(),
                Float.MAX_VALUE,
                new Vector2(),
                0,
                Float.MAX_VALUE,
                Float.MAX_VALUE
        );
    }

    public MovingEntity(
            String name,
            Object owner,
            World world,
            Vector2 position,
            Vector2 velocity,
            float maxSpeed,
            Vector2 heading,
            float mass,
            float maxTurnRate,
            float maxForce) {
        super(name, owner, world);
        this.position.set(position);
        this.velocity = new Vector2(velocity);
        this.maxSpeed = maxSpeed;
        this.heading = new Vector2(heading);
        this.mass = mass;
        this.maxTurnRate = maxTurnRate;
        this.maxForce = maxForce;
        this.side = new Vector2();
        this.type = "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * given a target position, this method rotates the entity's heading and
     * side vectors by an amount not greater than m_dMaxTurnRate until it
     * directly faces the target.
     *
     * @param target
     * @return true when the heading is facing in the desired direction
     */    public boolean rotateHeadingToFacePosition(Vector2 target) {
         Vector2 toTarget = target.cpy().sub(position);
         
         //first determine the angle between the heading vector and the target
         float angle = (float) Math.acos(heading.dot(toTarget));
         if (Float.isNaN(angle)) {
             angle = 0;
         }
         
         //return true if the player is facing the target
         if (angle < MINIMAL_TURN_ANGLE) {
             return true;
         }
         
         //clamp the amount to turn to the max turn rate
         if (angle > maxTurnRate) {
             angle = maxTurnRate;
         }
         
         //The next few lines use a rotation matrix to rotate the player's heading
         //vector accordingly
         Matrix3 RotationMatrix = new Matrix3();
         
         //notice how the direction of rotation has to be determined when creating
         //the rotation matrix
         RotationMatrix.rotate((angle * VectorUtilities.sign(heading, toTarget)));
         setHeading(transformVector2s(RotationMatrix, heading));
         velocity.set(transformVector2s(RotationMatrix, velocity));
         
         return false;
     }

    /**
     * first checks that the given heading is not a vector of zero length. If
     * the new heading is valid this function sets the entity's heading and side
     * vectors accordingly
     * @param newHeading
     */
     public void setHeading(Vector2 newHeading) {
         assert ((newHeading.len() - 1.0) < MINIMAL_TURN_ANGLE);
         
         heading.set(newHeading);
         
         //the side vector must always be perpendicular to the heading
         side.set(perpendicular(heading));
    }

     // accessors
     public float getMass() {
        return mass;
    }

     public void setMass(float mass) {
        this.mass = mass;
    }

     public float getMaxSpeed() {
         return maxSpeed;
    }

     public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

     public float getMaxForce() {
         return maxForce;
    }

     public void setMaxForce(float maxForce) {
         this.maxForce = maxForce;
    }

     public boolean IsSpeedMaxedOut() {
         return maxSpeed * maxSpeed >= velocity.len2();
     }
     
     public float getSpeed() {
         return velocity.len();
     }
     
     public float getSpeedSq() {
        return velocity.len2();
     }
     
     public float getMaxTurnRate() {
         return maxTurnRate;
     }
     
     public void setMaxTurnRate(float maxTurnRate) {
         this.maxTurnRate = maxTurnRate;
     }
     
     public Vector2 getVelocity() {
         return velocity.cpy();
     }

     public void setVelocity(Vector2 newVel) {
         velocity.set(newVel);
    }

    public Vector2 getHeading() {
        return heading.cpy();
    }

    public Vector2 getSide() {
        return side.cpy();
    }
}
